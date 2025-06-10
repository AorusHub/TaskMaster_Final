package com.example.taskmaster.api;

import com.example.taskmaster.api.response.UserResponse;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.CalendarEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("tasks")
    Call<List<Task>> getTasks();

    // Calendar API endpoints
    @GET("calendar/events")
    Call<List<CalendarEvent>> getCalendarEvents(@Query("start") String startDate,
                                                @Query("end") String endDate);

    @POST("calendar/events")
    Call<CalendarEvent> createCalendarEvent(@Body CalendarEvent event);

    @PUT("calendar/events/{eventId}")
    Call<CalendarEvent> updateCalendarEvent(@Path("eventId") String eventId,
                                            @Body CalendarEvent event);

    @DELETE("calendar/events/{eventId}")
    Call<Void> deleteCalendarEvent(@Path("eventId") String eventId);

    @GET("user")
    Call<UserResponse> getUserData();
}