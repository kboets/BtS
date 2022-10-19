INSERT INTO `country` (`country_code`, `country`, `flag`) VALUES
    ('BE', 'Belgium', 'https://media.api-sports.io/flags/be.svg');

INSERT INTO `league` (`league_id`, `name`, `start_season`, `end_season`, `current`, `season`, `country_code`, `logo`, `flag`, `selected`) VALUES
    (4366, 'Jupiler Pro League', '2022-07-22', '2023-04-21', '1', 2022, 'BE', 'https://media.api-sports.io/football/leagues/144.png', 'https://media.api-sports.io/flags/be.svg', '1');


INSERT INTO `team` (`id`, `team_id`, `name`, `stadium_name`, `city`, `stadium_capacity`, `logo`, `league_id`) VALUES
(77, 260, 'OH Leuven', 'King Power at Den Dreef Stadion', 'Heverlee', 12500, 'https://media.api-sports.io/football/teams/260.png', 4366),
(78, 261, 'KVC Westerlo', 'Het Kuipje', 'Westerlo', 8035, 'https://media.api-sports.io/football/teams/261.png', 4366),
(79, 266, 'KV Mechelen', 'AFAS-stadion Achter de Kazerne', 'Malines', 16715, 'https://media.api-sports.io/football/teams/266.png', 4366),
(80, 554, 'Anderlecht', 'Lotto Park', 'Brussel', 28063, 'https://media.api-sports.io/football/teams/554.png', 4366),
(81, 569, 'Club Brugge KV', 'Jan Breydelstadion', 'Brugge', 29062, 'https://media.api-sports.io/football/teams/569.png', 4366),
(82, 600, 'Zulte Waregem', 'Elindus Arena', 'Waregem', 12300, 'https://media.api-sports.io/football/teams/600.png', 4366),
(83, 624, 'Oostende', 'Diaz Arena', 'Oostende', 8432, 'https://media.api-sports.io/football/teams/624.png', 4366),
(84, 631, 'Gent', 'GHELAMCO-arena', 'Gent', 20000, 'https://media.api-sports.io/football/teams/631.png', 4366),
(85, 733, 'Standard Liege', 'Stade Maurice Dufrasne', 'Luik', 27670, 'https://media.api-sports.io/football/teams/733.png', 4366),
(86, 734, 'Kortrijk', 'Guldensporenstadion', 'Kortrijk', 9399, 'https://media.api-sports.io/football/teams/734.png', 4366),
(87, 735, 'St. Truiden', 'Stadion Stayen', 'St.-Trond', 14600, 'https://media.api-sports.io/football/teams/735.png', 4366),
(88, 736, 'Charleroi', 'Stade du Pays de Charleroi', 'Charleroi', 18000, 'https://media.api-sports.io/football/teams/736.png', 4366),
(89, 739, 'AS Eupen', 'Stadion am Kehrweg', 'Eupen', 8363, 'https://media.api-sports.io/football/teams/739.png', 4366),
(90, 740, 'Antwerp', 'Bosuilstadion', 'Deurne', 23057, 'https://media.api-sports.io/football/teams/740.png', 4366),
(91, 741, 'Cercle Brugge', 'Jan Breydelstadion', 'Brugge', 29062, 'https://media.api-sports.io/football/teams/741.png', 4366),
(92, 742, 'Genk', 'Cegeka Arena', 'Genk', 24956, 'https://media.api-sports.io/football/teams/742.png', 4366),
(93, 1393, 'Union St. Gilloise', 'Stade Joseph Mariën', 'Brussels', 9400, 'https://media.api-sports.io/football/teams/1393.png', 4366),
(94, 10244, 'Seraing United', 'Stade du Pairay', 'Seraing', 14326, 'https://media.api-sports.io/football/teams/10244.png', 4366);

INSERT INTO `round` (`round_id`, `round`, `season`, `league_id`, `current`, `actual_date`, `round_number`) VALUES
(977, 'Regular_Season_-_1', 2022, 4366, '0', NULL, 1),
(978, 'Regular_Season_-_2', 2022, 4366, '0', NULL, 2),
(979, 'Regular_Season_-_3', 2022, 4366, '0', NULL, 3),
(980, 'Regular_Season_-_4', 2022, 4366, '0', '2022-09-01', 4),
(981, 'Regular_Season_-_5', 2022, 4366, '0', '2022-08-25', 5),
(982, 'Regular_Season_-_6', 2022, 4366, '0', '2022-09-01', 6),
(983, 'Regular_Season_-_7', 2022, 4366, '1', '2022-10-14', 7),
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


