package com.example.taskmaster.api.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData userData;

    public static class UserData {
        @SerializedName("id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("profile_picture")
        private String profilePicture;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("last_login")
        private String lastLogin;

        @SerializedName("stats")
        private UserStats stats;

        public static class UserStats {
            @SerializedName("completed_tasks")
            private int completedTasks;

            @SerializedName("total_tasks")
            private int totalTasks;

            @SerializedName("pomodoro_sessions")
            private int pomodoroSessions;

            @SerializedName("pomodoro_minutes")
            private int pomodoroMinutes;

            @SerializedName("achievement_count")
            private int achievementCount;

            // Getters and Setters
            public int getCompletedTasks() {
                return completedTasks;
            }

            public void setCompletedTasks(int completedTasks) {
                this.completedTasks = completedTasks;
            }

            public int getTotalTasks() {
                return totalTasks;
            }

            public void setTotalTasks(int totalTasks) {
                this.totalTasks = totalTasks;
            }

            public int getPomodoroSessions() {
                return pomodoroSessions;
            }

            public void setPomodoroSessions(int pomodoroSessions) {
                this.pomodoroSessions = pomodoroSessions;
            }

            public int getPomodoroMinutes() {
                return pomodoroMinutes;
            }

            public void setPomodoroMinutes(int pomodoroMinutes) {
                this.pomodoroMinutes = pomodoroMinutes;
            }

            public int getAchievementCount() {
                return achievementCount;
            }

            public void setAchievementCount(int achievementCount) {
                this.achievementCount = achievementCount;
            }
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public UserStats getStats() {
            return stats;
        }

        public void setStats(UserStats stats) {
            this.stats = stats;
        }
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}