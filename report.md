# Report for assignment 3

This is a template for your report. You are free to modify it as needed.
It is not required to use markdown for your report either, but the report
has to be delivered in a standard, cross-platform format.

## Project

[OpenCensus](https://github.com/census-instrumentation/opencensus-java) is a telemetry framework. This is the Java library. Its main use is collecting metrics about the use of a program and exporting them.

## Onboarding experience

Project had build instructions in `CONTRIBUTING.md`. Following them with Java 13 gave an error. The file mentioned that the project uses Java 6, which isn't supported anymore. `openjdk-8-jdk` seemed to work, though. The local build then took 20 minutes on a laptop, but gave no errors.

## Complexity
Running

```
lizard --CCN 8 --languages java --sort cyclomatic_complexity --exclude "*/contrib/*" opencensus-java/
```

in a clean repo gives

```
  NLOC    CCN   token  PARAM  length  location  
------------------------------------------------
      20     14    124      1      20 Tracestate::validateKey@231-250@opencensus-java/api/src/main/java/io/opencensus/trace/Tracestate.java
      37     13    277      2      42 StackdriverExportUtils::setResourceForBuilder@510-551@opencensus-java/exporters/stats/stackdriver/src/main/java/io/opencensus/exporter/stats/stackdriver/StackdriverExportUtils.java
      47     12    383      2      53 TraceContextFormat::extract@111-163@opencensus-java/impl_core/src/main/java/io/opencensus/implcore/trace/propagation/TraceContextFormat.java
      31     11    176      2      32 Metric::checkTypeMatch@105-136@opencensus-java/api/src/main/java/io/opencensus/metrics/export/Metric.java
      49     10    351      1      49 VarInt::getVarLong@216-264@opencensus-java/impl_core/src/main/java/io/opencensus/implcore/internal/VarInt.java
      39     10    232      1      44 ServerStatsEncoding::parseBytes@81-124@opencensus-java/api/src/main/java/io/opencensus/common/ServerStatsEncoding.java
     104     10    742      4     113 PrometheusExportUtils::getSamples@148-260@opencensus-java/exporters/stats/prometheus/src/main/java/io/opencensus/exporter/stats/prometheus/PrometheusExportUtils.java
      56     10    542      2      56 JsonConversionUtils::convertToJson@111-166@opencensus-java/exporters/trace/elasticsearch/src/main/java/io/opencensus/exporter/trace/elasticsearch/JsonConversionUtils.java
      51     10    466      1      52 InstanaExporterHandler::convertToJson@125-176@opencensus-java/exporters/trace/instana/src/main/java/io/opencensus/exporter/trace/instana/InstanaExporterHandler.java
      32      9    227      1      39 BinaryFormatImpl::fromByteArray@109-147@opencensus-java/impl_core/src/main/java/io/opencensus/implcore/trace/propagation/BinaryFormatImpl.java
      30      9    217      1      30 TagContext::equals@63-92@opencensus-java/api/src/main/java/io/opencensus/tags/TagContext.java
      23      9    144      2      23 Duration::create@53-75@opencensus-java/api/src/main/java/io/opencensus/common/Duration.java
      43      9    408      2      52 ZipkinExporterHandler::generateSpan@105-156@opencensus-java/exporters/trace/zipkin/src/main/java/io/opencensus/exporter/trace/zipkin/ZipkinExporterHandler.java
```

We had three group members count the cyclomatic complexity of 4 methods. Our results varied, both among ourselves and compared to Lizard. We are not sure how that happened. It looks like Lizard might ignore `throw`, where some of us counted it as an exit point and deducted 1.

|                 | L | 1 | 2 | 3 |
|-----------------|---|---|---|---|
| generateSpan()  | 9 | 9 | 6 | 9 |
| create()        | 9 | 4 | 2 | 4 |
| equals()        | 9 | 9 | 7 | 7 |
| fromByteArray() | 9 | 4 | 4 | 4 |

OpenCensus can upload data to various backends. `ZipkinExporterHandler` contains static methods to aid in doing so for [Zipkin](https://github.com/openzipkin/zipkin). `generateSpan()` is one of them. It generates a [span](https://opencensus.io/tracing/span/), something like a single query, for the Zipkin library using the appropriate data. This method is undocumented. Could move out some checking.

`Duration` represents a span of time (not to be confused with the other span). Its static method `create()` instantiates one. This is done with a static method instead of a constructor because the AutoValue code generator is used. The documentation clearly states why an Exception might occur. Could improve complexity by using library methods.

`TagContext` is used to contain metadata about operations. `equals()` is a standard override comparing contained keys and values. The documentation clearly states that it will return true if they are the same and false otherwise.

`BinaryFormatImpl` is a helper class for `SpanContext`. `SpanContext` represents the associated state for a `Span`. `fromByteArray()` generates one from a byte array. The method is undocumented, but the method it overrides contains documentation explaining succinctly when Exceptions are thrown.

[ServerStatsEncoding](https://javadoc.io/doc/io.opencensus/opencensus-api/latest/io/opencensus/common/ServerStatsEncoding.html) is a class that encodes/decodes ServerStats (a representation of stats measured on the server side). The method `parseBytes()` decodes a serialized byte array. If the decoding succeed it returns the decoded value, otherwise null. 

[Tracestate](https://javadoc.io/static/io.opencensus/opencensus-api/0.25.0/io/opencensus/trace/Tracestate.html) is a class that carries tracing-system specific context in a list of key-value pairs. The method `validateKey()` verifies that a key is valid, i.e. follows a set of pre-defined rules.

[Metric](https://javadoc.io/static/io.opencensus/opencensus-api/0.25.0/io/opencensus/metrics/Metrics.html) is a class that represent a datamodel for what exporters takes as input. The method `checkTypeMatch()` check that the different arguments are of the correct type, i.e. Long, Double etc.

Instana is a APM that can be used for automatic visualization and performance analysis. The class `InstanaExporterHandler` handles a `Span` and exports its data in different formats. The class contains the function `convertToJson()` that converts the data to a JSON string.

None of the methods are outrageously long, but all of them except for `create()` could feasibly be shortened. Our opinion is that this project is mostly well factored already, with only small improvements possible.

## Coverage


### Tools

Adding the logging for DIY coverage was easy. However, the settings to show the logs were mistakenly added to the wrong `build.gradle`, which required some debugging. Because August had some experience using Unix tools, the processing of the logs went quickly.

### DYI
The lines added are shown in commits [`fe6eaa8`](https://github.com/augustjanse/opencensus-java/commit/fe6eaa84ddcaf5571836f17317e1bb6ab4422e4a) and [`30393ab`](https://github.com/augustjanse/opencensus-java/commit/30393ab48c4aea18ba9de9d10a814f450eb5aebe).

Running

```
./gradlew cleanTest test > logs
```

followed by 

```
grep -E -- 'validateKey|setResourceForBuilder|extract|checkTypeMatch|getVarLong|parseBytes|getSamples|convertToJson|convertToJson|fromByteArray|equals|create|generateSpan' logs | sort -u > results
```

followed by some minor processing results in `results`, which can be manually inspected to see what branches are missing.

The uncovered branches found are as following:

| Method                                     | Uncovered      | %     |
|--------------------------------------------|----------------|-------|
| `checkTypeMatch`                           | None           | 100%  |
| `convertToJson` (`InstanaExporterHandler`) | 2, 3, 5, 8     | 5/9   |
| `create`                                   | None           | 100%  |
| `equals`                                   | None           | 100%  |
| `extract`                                  | None           | 100%  |
| `fromByteArray`                            | None           | 100%  |
| `generateSpan`                             | 7              | 7/8   |
| `getSamples`                               | 2--6           | 1/6   |
| `convertToJson` (`JsonConversionUtils`)    | 1, 3, 6, 7, 11 | 6/11  |
| `parseBytes`                               | None           | 100%  |
| `setResourceForBuilder`                    | 1, 6, 8, 9     | 12/16 |
| `validateKey`                              | None           | 100%  |

### Evaluation

Logging was added to `if`, `while`, `catch` and `for` branches. Empty `else` blocks were added where needed. No attempt was made to log logical branches (`||` and `&&`). There were no ternary operations in the covered methods.

The two limitations to the tool is that it doesn't cover all kinds of branches and (mainly) that it is not really automatic.

3. Are the results of your tool consistent with existing coverage tools?

Yes, the results of our tools are consistent with the existing coverage tools. Test cases for branches which are uncovered previously are added without disturbing the original test cases. Therefore, the coverage rate has been improved. For example, regarding convertToJson (JsonConversionUtils), the previous test cases leave out branch 1,3,6,7,11. By using the newly added test cases, braches 1,3,6 can be tested.

### Coverage improvement

Show the comments that describe the requirements for the coverage.

Report of old coverage: [link](https://github.com/augustjanse/opencensus-java/blob/report-and-coverage/results)

Report of new coverage: [link](https://github.com/augustjanse/opencensus-java/blob/report-and-coverage/results2)

Test cases added are available in the following commits:

https://github.com/augustjanse/opencensus-java/commit/92d141991bfb9b93b35d5ea90c16be50eceda09f
https://github.com/augustjanse/opencensus-java/commit/b61b583a68a46002d28617df7362155b256f2313
https://github.com/augustjanse/opencensus-java/commit/47a91c272dd64a0e0be254a5d1d82380d5872efb
https://github.com/augustjanse/opencensus-java/commit/277c59495aff9f31a90ef6059e7b1e91b38bce6a (broken, fixed in https://github.com/augustjanse/opencensus-java/commit/c71f330d5c456ea79797ec149c305b32f65caddd )

## Refactoring

Plan for refactoring complex code:

Estimated impact of refactoring (lower CC, but other drawbacks?).

Carried out refactoring (optional)

git diff ...

## Overall experience

We learned the difficulty of supporting different Java version. One takeaway is that Google code generators (`AutoValue`) may have a lot of benefits, but also increase the threshold to get started in a project.

We think this project is a lot more complicated than some other projects, with lots of submodules, dependencies and requirements. We managed to dive into this project even so, and even though the code was well factored in the first place, we think we managed to make valuable refactorings. We think that should be counted for P+.
