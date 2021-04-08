package com.agoda.rate.utils.limitter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
@Getter
public class LimitRule {
    private final int durationSeconds;
    private final long limit;
    private final int stopDurationSeconds;

    private LimitRule(int durationSeconds, long limit,int stopDurationSeconds){
        this.durationSeconds = durationSeconds;
        this.limit = limit;
        this.stopDurationSeconds = stopDurationSeconds;
    }

    private static void checkDuration(Duration duration) {
        requireNonNull(duration, "duration can not be null");
        if (Duration.ofSeconds(1).compareTo(duration) > 0) {
            throw new IllegalArgumentException("duration must be great than 1 second");
        }
    }

    public static LimitRule of(Duration duration, long limit,Duration stopDuration) {
        checkDuration(duration);
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be greater than zero.");
        }
        int durationSeconds = (int) duration.getSeconds();
        int stopDurationSeconds = (int)stopDuration.getSeconds();
        return new LimitRule(durationSeconds, limit,stopDurationSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof LimitRule)) {
            return false;
        }
        LimitRule that = (LimitRule) o;
        return durationSeconds == that.durationSeconds
                && limit == that.limit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(durationSeconds, limit);
    }
}
