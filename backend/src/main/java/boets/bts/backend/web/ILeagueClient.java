package boets.bts.backend.web;

import boets.bts.backend.dto.LeagueDto;

import java.util.List;

public interface ILeagueClient {

    List<LeagueDto> retrieveAllLeagues(String countryCode, int year);
}
