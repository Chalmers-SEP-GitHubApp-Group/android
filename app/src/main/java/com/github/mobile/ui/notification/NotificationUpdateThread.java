package com.github.mobile.ui.notification;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * Created by Jacob on 2014-12-12.
 */
public class NotificationUpdateThread extends AsyncTask<Context, Void, List<Notification>>{

    private static String TAG = "NotificationUpdateThread";

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
        client.setCredentials("jake91", "XXXXXXX");
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
            //Look for changes and notify
            try{
                for (Repository repo : service.getRepositories()) {
                    List<RepositoryBranchWrapper> branchesForRepo = branchDAO.readForRepo(db, repo.getId());
                    if(branchesForRepo.size() == 0){
                        //new repo
                        repoDAO.create(db, repo);
                        notifications.add(new Notification(Notification.Type.NEW_REPOSITORY, repo.getName(), null));
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
                                        notifications.add(new Notification(Notification.Type.NEW_COMMIT, repo.getName(), branch.getName()));
                                    }
                                    break;
                                }else if(! it.hasNext()){
                                    //Gone through all and haven't found any branch with the same name
                                    //new branch!
                                    branchDAO.create(db, new RepositoryBranchWrapper(repo.getId(), branch));
                                    notifications.add(new Notification(Notification.Type.NEW_BRANCH, repo.getName(), branch.getName()));
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
            NotificationDataSource notificationDAO = new NotificationDataSource();
            notificationDAO.create(DatabaseManager.getInstance().getWritableDatabase(),notifications);
        }
    }
}
