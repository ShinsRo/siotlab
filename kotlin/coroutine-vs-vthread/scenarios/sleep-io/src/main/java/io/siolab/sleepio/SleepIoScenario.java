package io.siolab.sleepio;

public interface SleepIoScenario {
    SleepIoResult run(SleepIoRequest request) throws Exception;
}
