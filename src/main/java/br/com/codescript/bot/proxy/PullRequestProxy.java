package br.com.codescript.bot.proxy;

import br.com.codescript.bot.model.PullRequest;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.Optional;

public class PullRequestProxy {

    private Cache<String, PullRequest> cachedPullRequests;

    public PullRequestProxy() {
        this.cachedPullRequests = Cache2kBuilder
                .of(String.class, PullRequest.class)
                .name("pull-requests")
                .eternal(true)
                .build();
    }

    public PullRequest set(PullRequest pullRequest) {
        this.cachedPullRequests.put(pullRequest.getId(), pullRequest);
        return pullRequest;
    }

    public Optional<PullRequest> get(String id) {
        return Optional.ofNullable(cachedPullRequests.get(id));
    }

    public void remove(String id) {
        this.cachedPullRequests.remove(id);
    }
}
