# datapipes
Here comes another framework for creaiting simple "data pipes" to schedule collect-transform-send tasks.
## Motivation
*Why making another one ?*
My original task was to setup a service that will collect JMX metrics from several applications using [Jolokia](https://jolokia.org/) HTTP endpoint, make simple transformations to metric names and pass data to Graphite.
options I considered: 
a) use Apache Camel - found it too big for such simple task and lacking scheduling features
b) use [dimovelev/metrics-sampler](https://github.com/dimovelev/metrics-sampler) - almost exactly what was needed, but I didn't like the concept of using XStream for configuration.

## Design
*So, what is so special about this project ?*
Nothing that much, really. But I hope It will hit the mark with it's power of configuration.
The power is ... well... a Spring :-)
The idea is that there is no separate "configuration", that isolates the internals of system from developer. What you need to configure is a groovy file with beans for Spring ApplicationContext. And so, What-You-Config-Is-What-You-Get. Nothing more.

##Usage
* build the project with 'gradle fatJar'
* run the jar telling the JVM where to pick the config(context) file with the "configLocation" option
like this: `java -DconfigLocation=./src/main/resources/examples/jolokia2graphite.example.groovy -jar ./build/libs/datapipes-all.jar 
`

Here's the example of config for my original task:

```groovy
import org.springframework.scheduling.support.CronTrigger
//... some more imports

beans {

    myCollector(JolokiaCollector) {
        jolokiaUrl = 'http://localhost:10987/bpcservlet/jmx/metrics'
        authenticator = new CustomHeaderDigestAuthenticator()
        authenticator.secret = 'asjdhsaf783gaugsfy92hf'
    }
    mySender(GraphiteSender) {
        host = 'b64bacd9.carbon.hostedgraphite.com'
        port = 2003
    }

    jolokia2MyHostedGraphite(SimpleDataBridge) {
        collector = myCollector
        sender = mySender
        transformer = new KeyReplaceTransformer()
        transformer.prefix = "73a6d152-3d94-4803-9811-d37c7b7bfc2f"
    }

    myTask(DataTransferTask) {
        dataBridge = jolokia2MyHostedGraphite
        trigger = new CronTrigger("0 0/1 * 1/1 * ?")
        dataRequest = new JolokiaDataRequest(
                null, null, "org.apache.http.impl.conn.ApacheHttpClientPoolInfo:type=ApacheHttpClientPoolInfo")
    }

}
```
*Here's a picture instead of 1024 words:*
![diagram](https://github.com/stropa/datapipes/blob/master/src/main/resources/docs/overview.png)

