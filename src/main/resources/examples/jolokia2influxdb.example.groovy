package examples

import org.influxdb.InfluxDBFactory
import org.springframework.scheduling.support.CronTrigger
import org.stropa.data.bridge.jolokia.SimpleDataBridge
import org.stropa.data.collect.jolokia.JolokiaCollector
import org.stropa.data.collect.jolokia.JolokiaDataRequest
import org.stropa.data.collect.jolokia.auth.CustomHeaderDigestAuthenticator
import org.stropa.data.schedule.DataTransferTask
import org.stropa.data.send.KeyReplaceTransformer
import org.stropa.data.send.influxdb.InfluxDBSender

beans {

    myCollector(JolokiaCollector) {
        jolokiaUrl = 'http://localhost:10987/bpcservlet/jmx/metrics'
        authenticator = new CustomHeaderDigestAuthenticator()
        authenticator.secret = 'asjdhsaf783gaugsfy92hf'
    }

    influxDBSender(InfluxDBSender) {
        influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        databaseName = "play"
    }

    bridge(SimpleDataBridge) {
        collector = myCollector
        sender = influxDBSender
        transformer = new KeyReplaceTransformer()
        transformer.toReplace = [':', '=', "."]
        transformer.toReplaceWith = ['_', '_', '_']
    }

    myTask(DataTransferTask) {
        dataBridge = bridge
        trigger = new CronTrigger("0/5 * * 1/1 * ?")
        dataRequest = new JolokiaDataRequest(
                null, null, "org.apache.http.impl.conn.ApacheHttpClientPoolInfo:type=ApacheHttpClientPoolInfo")
    }

}