INSERT INTO `country` (`country_code`, `country`, `flag`) VALUES
                                                              ('BE', 'Belgium', 'https://media.api-sports.io/flags/be.svg');

INSERT INTO `league` (`league_id`, `name`, `start_season`, `end_season`, `current`, `season`, `country_code`, `logo`, `flag`, `selected`) VALUES
(4366, 'Jupiler Pro League', '2022-07-22', '2023-04-21', '1', 2022, 'BE', 'https://media.api-sports.io/football/leagues/144.png', 'https://media.api-sports.io/flags/be.svg', '1');


INSERT INTO `team` (`id`, `team_id`, `name`, `stadium_name`, `city`, `stadium_capacity`, `logo`, `league_id`) VALUES
  (569, 260, 'OH Leuven', 'King Power at Den Dreef Stadion', 'Heverlee', 12500, 'https://media.api-sports.io/football/teams/260.png', 4366),
  (570, 261, 'KVC Westerlo', 'Het Kuipje', 'Westerlo', 8035, 'https://media.api-sports.io/football/teams/261.png', 4366),
  (571, 266, 'KV Mechelen', 'AFAS-stadion Achter de Kazerne', 'Malines', 16715, 'https://media.api-sports.io/football/teams/266.png', 4366),
  (572, 554, 'Anderlecht', 'Lotto Park', 'Brussel', 28063, 'https://media.api-sports.io/football/teams/554.png', 4366),
  (573, 569, 'Club Brugge KV', 'Jan Breydelstadion', 'Brugge', 29062, 'https://media.api-sports.io/football/teams/569.png', 4366),
  (574, 600, 'Zulte Waregem', 'Elindus Arena', 'Waregem', 12300, 'https://media.api-sports.io/football/teams/600.png', 4366),
  (575, 624, 'Oostende', 'Diaz Arena', 'Oostende', 8432, 'https://media.api-sports.io/football/teams/624.png', 4366),
  (576, 631, 'Gent', 'GHELAMCO-arena', 'Gent', 20000, 'https://media.api-sports.io/football/teams/631.png', 4366),
  (577, 733, 'Standard Liege', 'Stade Maurice Dufrasne', 'Luik', 27670, 'https://media.api-sports.io/football/teams/733.png', 4366),
  (578, 734, 'Kortrijk', 'Guldensporenstadion', 'Kortrijk', 9399, 'https://media.api-sports.io/football/teams/734.png', 4366),
  (579, 735, 'St. Truiden', 'Stadion Stayen', 'St.-Trond', 14600, 'https://media.api-sports.io/football/teams/735.png', 4366),
  (580, 736, 'Charleroi', 'Stade du Pays de Charleroi', 'Charleroi', 14967, 'https://media.api-sports.io/football/teams/736.png', 4366),
  (581, 739, 'AS Eupen', 'Stadion am Kehrweg', 'Eupen', 8363, 'https://media.api-sports.io/football/teams/739.png', 4366),
  (582, 740, 'Antwerp', 'Bosuilstadion', 'Deurne', 23057, 'https://media.api-sports.io/football/teams/740.png', 4366),
  (583, 741, 'Cercle Brugge', 'Jan Breydelstadion', 'Brugge', 29062, 'https://media.api-sports.io/football/teams/741.png', 4366),
  (584, 742, 'Genk', 'Cegeka Arena', 'Genk', 24956, 'https://media.api-sports.io/football/teams/742.png', 4366),
  (585, 1393, 'Union St. Gilloise', 'Stade Joseph Mariën', 'Brussels', 9400, 'https://media.api-sports.io/football/teams/1393.png', 4366),
  (586, 10244, 'Seraing United', 'Stade du Pairay', 'Seraing', 14326, 'https://media.api-sports.io/football/teams/10244.png', 4366);

INSERT INTO `round` (`round_id`, `round`, `season`, `league_id`, `current`, `actual_date`, `round_number`) VALUES
(977, 'Regular_Season_-_1', 2022, 4366, '0', NULL, 1),
(978, 'Regular_Season_-_2', 2022, 4366, '0', NULL, 2),
(979, 'Regular_Season_-_3', 2022, 4366, '0', NULL, 3),
(980, 'Regular_Season_-_4', 2022, 4366, '0', '2022-09-01', 4),
(981, 'Regular_Season_-_5', 2022, 4366, '0', '2022-08-25', 5),
(982, 'Regular_Season_-_6', 2022, 4366, '0', '2022-09-01', 6),
(983, 'Regular_Season_-_7', 2022, 4366, '1', '2022-09-06', 7),
(984, 'Regular_Season_-_8', 2022, 4366, '0', NULL, 8),
(985, 'Regular_Season_-_9', 2022, 4366, '0', NULL, 9),
(986, 'Regular_Season_-_10', 2022, 4366, '0', NULL, 10),
(987, 'Regular_Season_-_11', 2022, 4366, '0', NULL, 11),
(988, 'Regular_Season_-_12', 2022, 4366, '0', NULL, 12),
(989, 'Regular_Season_-_13', 2022, 4366, '0', NULL, 13),
(990, 'Regular_Season_-_14', 2022, 4366, '0', NULL, 14),
(991, 'Regular_Season_-_15', 2022, 4366, '0', NULL, 15),
(992, 'Regular_Season_-_16', 2022, 4366, '0', NULL, 16),
(993, 'Regular_Season_-_17', 2022, 4366, '0', NULL, 17),
(994, 'Regular_Season_-_18', 2022, 4366, '0', NULL, 18),
(995, 'Regular_Season_-_19', 2022, 4366, '0', NULL, 19),
(996, 'Regular_Season_-_20', 2022, 4366, '0', NULL, 20),
(997, 'Regular_Season_-_21', 2022, 4366, '0', NULL, 21),
(998, 'Regular_Season_-_22', 2022, 4366, '0', NULL, 22),
(999, 'Regular_Season_-_23', 2022, 4366, '0', NULL, 23),
(1000, 'Regular_Season_-_24', 2022, 4366, '0', NULL, 24),
(1001, 'Regular_Season_-_25', 2022, 4366, '0', NULL, 25),
(1002, 'Regular_Season_-_26', 2022, 4366, '0', NULL, 26),
(1003, 'Regular_Season_-_27', 2022, 4366, '0', NULL, 27),
(1004, 'Regular_Season_-_28', 2022, 4366, '0', NULL, 28),
(1005, 'Regular_Season_-_29', 2022, 4366, '0', NULL, 29),
(1006, 'Regular_Season_-_30', 2022, 4366, '0', NULL, 30),
(1007, 'Regular_Season_-_31', 2022, 4366, '0', NULL, 31),
(1008, 'Regular_Season_-_32', 2022, 4366, '0', NULL, 32),
(1009, 'Regular_Season_-_33', 2022, 4366, '0', NULL, 33),
(1010, 'Regular_Season_-_34', 2022, 4366, '0', NULL, 34);

INSERT INTO `result` (`result_id`, `event_date`, `goals_home_team`, `goals_away_team`, `league_id`, `hometeam_id`, `awayteam_id`, `round`, `status`, `round_number`) VALUES
    (2513, '2022-07-22', 2, 2, 4366, 577, 576, 'Regular_Season_-_1', 'Match Finished', 1),
	(2514, '2022-07-23', 3, 1, 4366, 580, 581, 'Regular_Season_-_1', 'Match Finished', 1),
	(2515, '2022-07-23', 0, 2, 4366, 578, 569, 'Regular_Season_-_1', 'Match Finished', 1),
	(2516, '2022-07-23', 2, 0, 4366, 574, 586, 'Regular_Season_-_1', 'Match Finished', 1),
	(2517, '2022-07-23', 1, 1, 4366, 579, 585, 'Regular_Season_-_1', 'Match Finished', 1),
	(2518, '2022-07-24', 3, 2, 4366, 573, 584, 'Regular_Season_-_1', 'Match Finished', 1),
	(2519, '2022-07-24', 0, 2, 4366, 571, 582, 'Regular_Season_-_1', 'Match Finished', 1),
	(2520, '2022-07-24', 2, 0, 4366, 572, 575, 'Regular_Season_-_1', 'Match Finished', 1),
	(2521, '2022-07-24', 2, 0, 4366, 570, 583, 'Regular_Season_-_1', 'Match Finished', 1),
	(2522, '2022-07-29', 1, 0, 4366, 585, 580, 'Regular_Season_-_2', 'Match Finished', 2),
	(2523, '2022-07-30', 1, 0, 4366, 583, 572, 'Regular_Season_-_2', 'Match Finished', 2),
	(2524, '2022-07-30', 2, 1, 4366, 575, 571, 'Regular_Season_-_2', 'Match Finished', 2),
	(2525, '2022-07-30', 2, 0, 4366, 569, 570, 'Regular_Season_-_2', 'Match Finished', 2),
	(2526, '2022-07-30', 1, 1, 4366, 576, 579, 'Regular_Season_-_2', 'Match Finished', 2),
	(2527, '2022-07-31', 3, 1, 4366, 584, 577, 'Regular_Season_-_2', 'Match Finished', 2),
	(2528, '2022-07-31', 2, 1, 4366, 581, 573, 'Regular_Season_-_2', 'Match Finished', 2),
	(2529, '2022-07-31', 0, 1, 4366, 586, 578, 'Regular_Season_-_2', 'Match Finished', 2),
	(2530, '2022-07-31', 1, 0, 4366, 582, 574, 'Regular_Season_-_2', 'Match Finished', 2),
	(2531, '2022-08-05', 1, 1, 4366, 573, 574, 'Regular_Season_-_3', 'Match Finished', 3),
	(2532, '2022-08-06', 4, 2, 4366, 584, 581, 'Regular_Season_-_3', 'Match Finished', 3),
	(2533, '2022-08-06', 0, 0, 4366, 578, 579, 'Regular_Season_-_3', 'Match Finished', 3),
	(2534, '2022-08-06', 1, 3, 4366, 580, 575, 'Regular_Season_-_3', 'Match Finished', 3),
	(2535, '2022-08-06', 3, 0, 4366, 571, 585, 'Regular_Season_-_3', 'Match Finished', 3),
	(2536, '2022-08-07', 2, 0, 4366, 577, 583, 'Regular_Season_-_3', 'Match Finished', 3),
	(2537, '2022-08-07', 2, 1, 4366, 576, 570, 'Regular_Season_-_3', 'Match Finished', 3),
	(2538, '2022-08-07', 3, 1, 4366, 572, 586, 'Regular_Season_-_3', 'Match Finished', 3),
	(2539, '2022-08-07', 4, 2, 4366, 582, 569, 'Regular_Season_-_3', 'Match Finished', 3),
	(2540, '2022-08-12', 1, 3, 4366, 575, 576, 'Regular_Season_-_4', 'Match Finished', 4),
	(2541, '2022-08-13', 4, 2, 4366, 570, 577, 'Regular_Season_-_4', 'Match Finished', 4),
	(2542, '2022-08-13', 0, 0, 4366, 583, 571, 'Regular_Season_-_4', 'Match Finished', 4),
	(2543, '2022-08-13', 0, 1, 4366, 586, 580, 'Regular_Season_-_4', 'Match Finished', 4),
	(2544, '2022-08-13', 2, 1, 4366, 585, 578, 'Regular_Season_-_4', 'Match Finished', 4),
	(2545, '2022-08-14', 0, 3, 4366, 579, 572, 'Regular_Season_-_4', 'Match Finished', 4),
	(2546, '2022-08-14', 0, 1, 4366, 581, 582, 'Regular_Season_-_4', 'Match Finished', 4),
	(2547, '2022-08-14', 0, 3, 4366, 569, 573, 'Regular_Season_-_4', 'Match Finished', 4),
	(2548, '2022-08-14', 1, 4, 4366, 574, 584, 'Regular_Season_-_4', 'Match Finished', 4),
	(2549, '2022-08-19', 1, 3, 4366, 581, 586, 'Regular_Season_-_5', 'Match Finished', 5),
	(2550, '2022-08-20', 0, 1, 4366, 575, 579, 'Regular_Season_-_5', 'Match Finished', 5),
	(2551, '2022-08-20', 2, 1, 4366, 584, 583, 'Regular_Season_-_5', 'Match Finished', 5),
	(2552, '2022-08-21', 2, 1, 4366, 573, 578, 'Regular_Season_-_5', 'Match Finished', 5),
	(2553, '2022-08-21', 1, 3, 4366, 574, 580, 'Regular_Season_-_5', 'Match Finished', 5),
	(2554, '2022-08-21', 1, 3, 4366, 577, 569, 'Regular_Season_-_5', 'Match Finished', 5),
	(2555, '2022-08-21', 5, 4, 4366, 571, 570, 'Regular_Season_-_5', 'Match Finished', 5),
	(2556, '2022-08-26', 1, 3, 4366, 580, 573, 'Regular_Season_-_6', 'Match Finished', 6),
	(2557, '2022-08-27', 0, 4, 4366, 586, 584, 'Regular_Season_-_6', 'Match Finished', 6),
	(2558, '2022-08-27', 1, 1, 4366, 583, 574, 'Regular_Season_-_6', 'Match Finished', 6),
	(2559, '2022-08-27', 2, 1, 4366, 569, 575, 'Regular_Season_-_6', 'Match Finished', 6),
	(2560, '2022-08-27', 3, 1, 4366, 579, 571, 'Regular_Season_-_6', 'Match Finished', 6),
	(2561, '2022-08-28', 1, 2, 4366, 576, 582, 'Regular_Season_-_6', 'Match Finished', 6),
	(2562, '2022-08-28', 0, 1, 4366, 578, 577, 'Regular_Season_-_6', 'Match Finished', 6),
	(2563, '2022-08-28', 2, 1, 4366, 585, 572, 'Regular_Season_-_6', 'Match Finished', 6),
	(2564, '2022-08-28', 0, 1, 4366, 570, 581, 'Regular_Season_-_6', 'Match Finished', 6),
	(2565, '2022-08-31', 4, 2, 4366, 582, 585, 'Regular_Season_-_5', 'Match Finished', 5),
	(2566, '2022-09-01', 0, 1, 4366, 572, 576, 'Regular_Season_-_5', 'Match Finished', 5),
	(2567, '2022-09-02', 4, 0, 4366, 573, 583, 'Regular_Season_-_7', 'Match Finished', 7),
	(2568, '2022-09-03', 0, 0, 4366, 584, 579, 'Regular_Season_-_7', 'Match Finished', 7),
	(2569, '2022-09-03', 2, 3, 4366, 571, 586, 'Regular_Season_-_7', 'Match Finished', 7),
	(2570, '2022-09-03', 0, 1, 4366, 581, 578, 'Regular_Season_-_7', 'Match Finished', 7),
	(2571, '2022-09-03', 1, 0, 4366, 577, 575, 'Regular_Season_-_7', 'Match Finished', 7),
	(2572, '2022-09-04', 2, 2, 4366, 572, 569, 'Regular_Season_-_7', 'Match Finished', 7),
	(2573, '2022-09-04', 3, 0, 4366, 582, 570, 'Regular_Season_-_7', 'Match Finished', 7),
	(2574, '2022-09-04', 2, 1, 4366, 580, 576, 'Regular_Season_-_7', 'Match Finished', 7),
	(2575, '2022-09-04', 1, 3, 4366, 574, 585, 'Regular_Season_-_7', 'Match Finished', 7);