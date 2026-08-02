package com.facilityflow.config;

import com.facilityflow.entity.TicketPriority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Binds {@code app.ticket.escalation-hours} from application.yml into a
 * proper typed map. Deliberately using {@code @ConfigurationProperties}
 * here rather than a {@code @Value("#{${...}}")} SpEL trick — the SpEL
 * approach to inject a nested YAML map is fragile (Boot's relaxed binding
 * flattens nested maps into dotted properties, which the SpEL map-literal
 * parser does not reliably reconstruct), while ConfigurationProperties
 * binds nested maps natively and is the supported mechanism for this.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.ticket")
public class TicketEscalationProperties {

    /**
     * Hours a ticket may remain open (unescalated) before it breaches SLA,
     * keyed by priority. Falls back to 72 hours for any priority not
     * explicitly configured.
     */
    private Map<TicketPriority, Integer> escalationHours = new EnumMap<>(TicketPriority.class);

    public int hoursFor(TicketPriority priority) {
        return escalationHours.getOrDefault(priority, 72);
    }
}
