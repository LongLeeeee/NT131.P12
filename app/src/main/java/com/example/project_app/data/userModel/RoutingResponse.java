package com.example.project_app.data.userModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoutingResponse {

    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public class Route {
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("distance")
        private double distance;
        @SerializedName("duration")
        private double duration;
        public Geometry getGeometry() {
            return geometry;
        }
        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }

    public class Geometry {
        @SerializedName("coordinates")
        private List<List<Double>> coordinates;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }
    }
}