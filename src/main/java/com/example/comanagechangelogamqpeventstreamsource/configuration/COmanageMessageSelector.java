package com.example.comanagechangelogamqpeventstreamsource.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

import java.io.IOException;

@Slf4j
@Configuration
public class COmanageMessageSelector implements MessageSelector {

    @Value("${com.example.comanage.publish.CoGroup}")
    private Boolean selectCoGroup;
    @Value("${com.example.comanage.publish.CoPerson}")
    private Boolean selectCoPerson;
    @Value("${com.example.comanage.publish.allJson}")
    private Boolean selectAllJson;
    @Value("${com.example.comanage.publish.allStrings}")
    private Boolean selectAllStrings;

    public boolean accept(Message<?> message) {
        if (message.getPayload() instanceof String && meetsSelectionCriteria(message)) {
            return true;
        }
        return false;
    }

    // This method applies the preferences expressed in the property file to determine which strings logged
    // by the COmanage Changelog Provisioning Plugin should be sent to the AMQP exchange.  If you are re-purposing
    // this then modify the logic to return false for events you would like to drop.
    private boolean meetsSelectionCriteria(Message<?> message) {

        String payload = (String) message.getPayload();
        boolean payloadIsJSON = true;

        // if the property file specifies publishing all strings then select this message
        if (selectAllStrings) {
            log.info("comanage-changelog-processor: accepting " + payload);
            return true;
        }

        // map the message payload to a JSON object if possible
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(payload);

            // If you would like to view nicely formatted JSON objects as they pass by
            String formattedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            log.debug(formattedJson);

            if (selectAllJson) {
                // if the property file specifies publishing all JSON then select this message
                log.info("comanage-changelog-processor: accepting " + payload);
                return true;
            }
        } catch (IOException e) {
            payloadIsJSON = false;
        }

        if (payloadIsJSON) {
            // Cursory (non-schema) comparison to COmanage object types
            boolean payloadIsCoGroup = (jsonNode.get("CoGroup") != null) ? true : false;
            boolean payloadIsCoPerson = (jsonNode.get("CoPerson") != null) ? true : false;

            // if the property file specifies publishing CoGroups and this is a CoGroup then select this message
            if (payloadIsCoGroup && selectCoGroup) {
                log.info("comanage-changelog-processor: accepting " + payload);
                return true;
            }
            // if the property file specifies publishing CoPersons and this is a CoPerson then select this message
            else if (payloadIsCoPerson && selectCoPerson) {
                log.info("comanage-changelog-processor: accepting " + payload);
                return true;
            }
        }

        // this message did not meet criteria so drop it
        log.info("comanage-changelog-processor: rejecting " + payload);
        return false;
    }

}
