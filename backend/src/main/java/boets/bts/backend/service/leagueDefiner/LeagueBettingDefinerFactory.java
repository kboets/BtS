package boets.bts.backend.service.leagueDefiner;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LeagueBettingDefinerFactory {

    private Map<String, LeagueBettingDefiner> definerMap;

    public LeagueBettingDefinerFactory() {
        definerMap = new HashMap<>();
        definerMap.put("BE", new BELeagueBettingDefiner());
        definerMap.put("GB", new GBLeagueBettingDefiner());
        definerMap.put("IT", new ITLeagueBettingDefiner());
        definerMap.put("NL", new NLLeagueBettingDefiner());
        definerMap.put("ES", new ESLeagueBettingDefiner());
        definerMap.put("DE", new DELeagueBettingDefiner());
        definerMap.put("FR", new FRLeagueBettingDefiner());
        definerMap.put("PT", new PTLeagueBettingDefiner());
        definerMap.put("SE", new SELeagueBettingDefiner());
        definerMap.put("BR", new BRLeagueBettingDefiner());
        definerMap.put("NO", new NOLeagueBettingDefiner());
        definerMap.put("KR", new KRLeagueBettingDefiner());
        definerMap.put("CN", new CNLeagueBettingDefiner());
        definerMap.put("US", new USLeagueBettingDefiner());
        definerMap.put("JP", new JPLeagueBettingDefiner());
    }
    public LeagueBettingDefiner retieveLeagueDefiner(String countryCode) {
        LeagueBettingDefiner leagueBettingDefiner = definerMap.getOrDefault(countryCode, null);
        if (leagueBettingDefiner == null) {
            throw new IllegalArgumentException(String.format("Country code %s not yet implemented", countryCode));
        }
        return leagueBettingDefiner;
    }

    public List<String> getAllowedCountryCodes() {
        return new ArrayList<>(definerMap.keySet());
    }
}
