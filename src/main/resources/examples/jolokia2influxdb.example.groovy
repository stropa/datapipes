package examples

import org.influxdb.InfluxDBFactory
import org.springframework.scheduling.support.CronTrigger
import org.stropa.data.bridge.SimpleDataBridge
import org.stropa.data.collect.jolokia.JolokiaCollector
import org.stropa.data.collect.jolokia.JolokiaDataRequest
import org.stropa.data.collect.jolokia.auth.CustomHeaderDigestAuthenticator
import org.stropa.data.schedule.DataTransferTask
import org.stropa.data.transform.AddValueForSqlWeightTransformer
import org.stropa.data.transform.GroupByNamePartTransformer
import org.stropa.data.transform.KeyReplaceTransformer
import org.stropa.data.send.influxdb.InfluxDBSender

beans {

    myCollector(JolokiaCollector) {
        jolokiaUrl = 'http://localhost:8989/payment/jmx'
        authenticator = new CustomHeaderDigestAuthenticator()
        authenticator.secret = 'Scnsdsuigsg3g36w76s78'
    }

    influxDBSender(InfluxDBSender) {
        influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        databaseName = "play"
    }

    keyReplaceTransformer(KeyReplaceTransformer) {
        toReplace = ["metrics:name=\"",':', '=', ".", "(", ")", ",", "\"", "\\", "?", "*", "'", "<", ">"]
        toReplaceWith = ["", "_", "_", "_", "_", "_", "_", "_", "_", "_", "star", "", "le", "ge"]
        trimTo = 80
    }
    groupFieldsTransformer(GroupByNamePartTransformer) {
        splittingRegexp = "_(?=[^_]*\$)" // last entry of '_'
    }
    addValueForSqlWeightTransformer(AddValueForSqlWeightTransformer)

    bridge(SimpleDataBridge) {
        collector = myCollector
        sender = influxDBSender
        transformers = [keyReplaceTransformer, groupFieldsTransformer, addValueForSqlWeightTransformer]
    }

    myTask(DataTransferTask) {
        dataBridge = bridge
        trigger = new CronTrigger("0/5 * * 1/1 * ?")
        dataRequest = new JolokiaDataRequest(
                null, null, "metrics:name=sql_*")
    }

}