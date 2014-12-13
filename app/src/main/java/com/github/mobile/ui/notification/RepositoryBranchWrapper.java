package com.github.mobile.ui.notification;

import org.eclipse.egit.github.core.RepositoryBranch;


public class RepositoryBranchWrapper extends RepositoryBranch {
    private int id;
    private long repoId;
    public RepositoryBranchWrapper(){
        super();
    }

    public RepositoryBranchWrapper(long repoId){
        super();
        this.repoId = repoId;
    }

    public RepositoryBranchWrapper(long repoId, RepositoryBranch branch){
        super();
        this.repoId = repoId;
        super.setName(branch.getName());
        super.setCommit(branch.getCommit());
    }

    public void setRepoId(int repoId){
        this.repoId = repoId;
    }

    public long getRepoId(){
        return this.repoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
