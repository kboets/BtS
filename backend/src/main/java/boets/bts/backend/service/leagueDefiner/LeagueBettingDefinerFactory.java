package boets.bts.backend.service.leagueDefiner;

import org.springframework.stereotype.Component;

@Component
public class LeagueBettingDefinerFactory {

    public LeagueBettingDefiner retieveLeagueDefiner(String countryCode) {
        switch (countryCode) {
            case "BE": return new BELeagueBettingDefiner();
            case "GB": return new GBLeagueBettingDefiner();
            case "IT": return new ITLeagueBettingDefiner();
            case "NL": return new NLLeagueBettingDefiner();
            case "ES": return new ESLeagueBettingDefiner();
            case "DE": return new DELeagueBettingDefiner();
            case "FR": return new FRLeagueBettingDefiner();
            case "PT": return new PTLeagueBettingDefiner();

            default: throw new IllegalArgumentException(String.format("Country code %s not yet implemented", countryCode));
        }
    }
}
