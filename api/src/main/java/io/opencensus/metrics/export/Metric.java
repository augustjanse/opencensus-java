/*
 * Copyright 2018, OpenCensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opencensus.metrics.export;

import com.google.auto.value.AutoValue;
import io.opencensus.common.ExperimentalApi;
import io.opencensus.internal.Utils;
import io.opencensus.metrics.export.Value.ValueDistribution;
import io.opencensus.metrics.export.Value.ValueDouble;
import io.opencensus.metrics.export.Value.ValueLong;
import io.opencensus.metrics.export.Value.ValueSummary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

/**
 * A {@link Metric} with one or more {@link TimeSeries}.
 *
 * @since 0.17
 */
@ExperimentalApi
@Immutable
@AutoValue
public abstract class Metric {

  Metric() {}

  /**
   * Creates a {@link Metric}.
   *
   * @param metricDescriptor the {@link MetricDescriptor}.
   * @param timeSeriesList the {@link TimeSeries} list for this metric.
   * @return a {@code Metric}.
   * @since 0.17
   */
  public static Metric create(MetricDescriptor metricDescriptor, List<TimeSeries> timeSeriesList) {
    Utils.checkListElementNotNull(
        Utils.checkNotNull(timeSeriesList, "timeSeriesList"), "timeSeries");
    return createInternal(
        metricDescriptor, Collections.unmodifiableList(new ArrayList<TimeSeries>(timeSeriesList)));
  }

  /**
   * Creates a {@link Metric}.
   *
   * @param metricDescriptor the {@link MetricDescriptor}.
   * @param timeSeries the single {@link TimeSeries} for this metric.
   * @return a {@code Metric}.
   * @since 0.17
   */
  public static Metric createWithOneTimeSeries(
      MetricDescriptor metricDescriptor, TimeSeries timeSeries) {
    return createInternal(
        metricDescriptor, Collections.singletonList(Utils.checkNotNull(timeSeries, "timeSeries")));
  }

  /**
   * Creates a {@link Metric}.
   *
   * @param metricDescriptor the {@link MetricDescriptor}.
   * @param timeSeriesList the {@link TimeSeries} list for this metric.
   * @return a {@code Metric}.
   * @since 0.17
   */
  private static Metric createInternal(
      MetricDescriptor metricDescriptor, List<TimeSeries> timeSeriesList) {
    Utils.checkNotNull(metricDescriptor, "metricDescriptor");
    checkTypeMatch(metricDescriptor.getType(), timeSeriesList);
    return new AutoValue_Metric(metricDescriptor, timeSeriesList);
  }

  /**
   * Returns the {@link MetricDescriptor} of this metric.
   *
   * @return the {@code MetricDescriptor} of this metric.
   * @since 0.17
   */
  public abstract MetricDescriptor getMetricDescriptor();

  /**
   * Returns the {@link TimeSeries} list for this metric.
   *
   * <p>The type of the {@link TimeSeries#getPoints()} must match {@link MetricDescriptor.Type}.
   *
   * @return the {@code TimeSeriesList} for this metric.
   * @since 0.17
   */
  public abstract List<TimeSeries> getTimeSeriesList();

  private static void checkTypeMatch(MetricDescriptor.Type type, List<TimeSeries> timeSeriesList) {

    // Iterate though the object timeSeriesList aslong as its != null
    for (TimeSeries timeSeries : timeSeriesList) {
      System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 1);

      // Iterate through timeSeries.getPoints aslong as its != null
      for (Point point : timeSeries.getPoints()) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 2);
        Value value = point.getValue();
        String valueClassName = "";

        // enter branch if the superclass for value != null
        if (value.getClass().getSuperclass() != null) { // work around nullness check
          System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 3);
          // AutoValue classes should always have a super class.
          valueClassName = value.getClass().getSuperclass().getSimpleName();
        }

        // enter switch block
        switch (type) {

            // fallthrough case, keeps going until it hits break statement
          case GAUGE_INT64:

            // enter branch if type is of value CUMULATIVE_INT64
          case CUMULATIVE_INT64:
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 4);
            Utils.checkArgument(
                value instanceof ValueLong, "Type mismatch: %s, %s.", type, valueClassName);
            break;

            // fallthrough case, keeps going until it hits break statement
          case CUMULATIVE_DOUBLE:

            // enter branch if type is of value GAUGE_DOUBLE and break from switch block
          case GAUGE_DOUBLE:
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 5);
            Utils.checkArgument(
                value instanceof ValueDouble, "Type mismatch: %s, %s.", type, valueClassName);
            break;

            // fallthrough case, keeps going until it hits break statement
          case GAUGE_DISTRIBUTION:

            // enter branch if type is of value CUMULATIVE_DISTRIBUTION and break from
            // switch block
          case CUMULATIVE_DISTRIBUTION:
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 6);
            Utils.checkArgument(
                value instanceof ValueDistribution, "Type mismatch: %s, %s.", type, valueClassName);
            break;

            // enter branch if type is of value SUMMARY
          case SUMMARY:
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + 7);
            Utils.checkArgument(
                value instanceof ValueSummary, "Type mismatch: %s, %s.", type, valueClassName);
        }
      }
    }
  }
}
