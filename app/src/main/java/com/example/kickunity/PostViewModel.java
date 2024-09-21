package com.example.kickunity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends ViewModel {

    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public void addPost(String title, String content, String category, String time) {
        List<Post> currentPosts = posts.getValue();
        if (currentPosts != null) {
            currentPosts.add(new Post(title, content, category, time));
            posts.setValue(currentPosts);
        }
    }

    public static class Post {
        public String title;
        public String content;
        public String category;
        public String time;

        public Post(String title, String content, String category, String time) {
            this.title = title;
            this.content = content;
            this.category = category;
            this.time = time;
        }
    }
}
