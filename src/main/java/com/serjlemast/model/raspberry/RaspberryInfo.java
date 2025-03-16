package com.serjlemast.model.raspberry;

public record RaspberryInfo(
    String deviceId,
    String boardModel,
    String operatingSystem,
    String javaVersions,
    double jvMemoryMb,
    double boardTemperature) {}
