package com.github.mobile.ui.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.mobile.ui.user.HomeActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import android.app.PendingIntent;
import javax.management.NotificationListener;

/**
 * Created by Jacob on 2014-12-12.
 */
public class NotificationUpdateThread extends AsyncTask<Context, Void, List<Notification>>{

    private static String TAG = "NotificationUpdateThread";

    private static NotificationManager notificationManager;

    public NotificationUpdateThread(){

    }

    @Override
    protected List<Notification> doInBackground(Context... params) {
        List<Notification> notifications = new ArrayList<Notification>();
        RepositoryDataSource repoDAO = new RepositoryDataSource();
        RepositoryBranchDataSource branchDAO = new RepositoryBranchDataSource();

        DBHelper dbHelper = DatabaseManager.getInstance(params[0]);

        SQLiteDatabase db0 = dbHelper.getReadableDatabase();
        Log.d(TAG, "open readable database");
        List<Repository> dbRepoList = repoDAO.read(db0, 100);

        GitHubClient client = new GitHubClient();
        client.setCredentials(Preferences.getUsername(), Preferences.getPassword());
        RepositoryService service = new RepositoryService(client);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "open writable database");

        //If the database is empty, just collect the data.
        if(dbRepoList.size() <= 0){
            try{
                List<Repository> repoListJustStore = service.getRepositories();
                repoDAO.createMany(db,repoListJustStore);
                for(Repository repo : repoListJustStore){
                    branchDAO.createMany(db, service.getBranches(repo), repo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //Look for changes and create notifications
            try{
                for (Repository repo : service.getRepositories()) {
                    List<RepositoryBranchWrapper> branchesForRepo = branchDAO.readForRepo(db, repo.getId());
                    if(branchesForRepo.size() == 0){
                        //new repo
                        repoDAO.create(db, repo);
                        notifications.add(new Notification(Notification.Type.NEW_REPOSITORY, repo.getName(), null, repo.getId()));
                    }else{
                        for(RepositoryBranch branch : service.getBranches(repo)){
                            Iterator<RepositoryBranchWrapper> it = branchesForRepo.iterator();
                            while(it.hasNext()){
                                RepositoryBranchWrapper comp = it.next();
                                if(branch.getName().equals(comp.getName())){
                                    if(! branch.getCommit().getSha().equals(comp.getCommit().getSha())){
                                        //new Commit to branch
                                        comp.setCommit(branch.getCommit()); // add the commit information to the old branch
                                        branchDAO.update(db, comp);
                                        Notification not = new Notification(Notification.Type.NEW_COMMIT, repo.getName(), branch.getName(), comp.getRepoId());
                                        not.setSha(branch.getCommit().getSha());
                                        notifications.add(not);
                                    }
                                    break;
                                }else if(! it.hasNext()){
                                    //Gone through all and haven't found any branch with the same name
                                    //new branch!
                                    branchDAO.create(db, new RepositoryBranchWrapper(repo.getId(), branch));
                                    Notification not = new Notification(Notification.Type.NEW_BRANCH, repo.getName(), branch.getName(), repo.getId());
                                    not.setSha(branch.getCommit().getSha());
                                    notifications.add(not);
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return notifications;
    }

    @Override
    protected void onPostExecute(List<Notification> notifications){
        if(notifications.size() > 0){
            //Since when the listener in notificationsDataSource will cause GUI updates, we writes to the database here in the MAIN thread.
            NotificationDataSource notificationDAO = new NotificationDataSource();
            notificationDAO.create(DatabaseManager.getInstance().getWritableDatabase(),notifications);
        }
    }

//    private void fireNotification(Context context, Notification notification){
//        Intent resultIntent = new Intent(context, HomeActivity.class);
//        resultIntent.setAction(VIEW.NOTIFICATION);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, new Long(notification.getId()).intValue(), resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        long[] pattern = {500,500,500,500,500,500,500,500,500};
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setContentIntent(contentIntent);
//        builder.setSmallIcon(drawable.app_icon);
//        builder.setContentTitle(notification.getContentTitle());
//        builder.setContentText(notification.getContentText());
////        if(Preferences.getSoundIsOn()){
////            builder.setSound(alarmSound);
////        }
//        builder.setVibrate(pattern);
//        builder.setAutoCancel(true);
//        builder.setWhen(notification.getDate().getTime());
//
//        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(new Long(notification.getId()).intValue(), builder.b);
//        notificationManager.not
//        for(NotificationListener listener : listenerList){
//            listener.notificationRecieved();
//        }
//    }
//
//    public static void viewedNotification(Notification notification){
//        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Log.d("PrincePolo", "cancel id: " + notification.getId());
//        notificationManager.cancel(notification.getId());
//    }
}
