package com.serjlemast.model.raspberry;

public record RaspberryInfo(
        String boardModel,
        String operatingSystem,
        String javaVersions,
        double jvMemoryMb,
        double boardTemperature) {}
