package boets.bts.backend.service.leagueDefiner;

import org.springframework.stereotype.Component;

@Component
public class LeagueBettingDefinerFactory {

    public LeagueBettingDefiner retieveLeagueDefiner(String countryCode) {
        switch (countryCode) {
            case "BE": return new BelgianLeagueBettingDefiner();
            default: throw new RuntimeException(String.format("Country code %s not yet implemented", countryCode));
        }
    }
}
