package com.gracenote.cae.sparkkuduload.program

import com.gracenote.cae.sparkkuduload.UnitSpec
import scala.language.reflectiveCalls  // to remove warning when interacting with fixture

class ProgramSpec extends UnitSpec{

  def fixture =
    new {
      val programJson: String = getProgramJson()
    }

  "Program" should "be instantiable from a json string" in {
    val json = fixture.programJson
    val program = Program.fromJson(json)
    assert(program.programId == 11111111)
  }

  def getProgramJson(): String = {
    """
      |{
      |  "programId" : 11111111,
      |  "programType" : "TT",
      |  "programSubType" : "TEAM EVENT",
      |  "genres" : [ "SOCCER" ],
      |  "originalSource" : "MULTI",
      |  "superSeriesProgramId" : 191265,
      |  "originalSourceId" : 11127,
      |  "category" : null,
      |  "productionCompanies" : [ ],
      |  "animation" : null,
      |  "holiday" : null,
      |  "countriesOfOrigin" : [ ],
      |  "originalAudioLanguages" : [ ],
      |  "industryNumbers" : [ ],
      |  "originalAirDate" : null,
      |  "credits" : [ ],
      |  "versions" : [ {
      |    "versionId" : 6674353,
      |    "runtime" : 0,
      |    "runtimeMs" : 0,
      |    "duration" : 0,
      |    "durationMs" : 0,
      |    "warnings" : [ ],
      |    "attributes" : [ ],
      |    "colorCode" : "COLOR",
      |    "imax" : false,
      |    "threeD" : false,
      |    "doNotUse" : false,
      |    "label" : "",
      |    "versionMappings" : [ {
      |      "versionMappingId" : 16985581,
      |      "tmsId" : "EP017861790210",
      |      "titlesetId" : 261975,
      |      "episodicTitlesetId" : 5176712,
      |      "descriptionLanguage" : "SPANISH",
      |      "teamName1Id" : 5469,
      |      "teamBrand1Id" : 4381,
      |      "teamName2Id" : 5877,
      |      "teamBrand2Id" : 4789,
      |      "venueNameId" : null,
      |      "venueBrandId" : null,
      |      "doNotUse" : false,
      |      "parentTmsId" : "SH017861790000"
      |    }, {
      |      "versionMappingId" : 16985580,
      |      "tmsId" : "EP000206851003",
      |      "titlesetId" : 261975,
      |      "episodicTitlesetId" : 5176712,
      |      "descriptionLanguage" : "ENGLISH",
      |      "teamName1Id" : 5469,
      |      "teamBrand1Id" : 4381,
      |      "teamName2Id" : 5877,
      |      "teamBrand2Id" : 4789,
      |      "venueNameId" : null,
      |      "venueBrandId" : null,
      |      "doNotUse" : false,
      |      "parentTmsId" : "SH000206850000"
      |    } ],
      |    "ratings" : [ ],
      |    "releases" : [ ]
      |  } ],
      |  "parentTmsId" : null,
      |  "parentProgramId" : 191265,
      |  "descriptions" : [ {
      |    "descriptionId" : 10213705,
      |    "text" : "From Cary, N.C.",
      |    "language" : "ENGLISH",
      |    "type" : "PLOT 40",
      |    "proofedBy" : null,
      |    "proofedDate" : null,
      |    "provider" : null,
      |    "showcard" : false
      |  } ],
      |  "titles" : [ {
      |    "titlesetId" : 261975,
      |    "type" : "MAIN",
      |    "language" : "ENGLISH",
      |    "text" : "College Soccer",
      |    "reducedTitles" : [ "Soccer" ],
      |    "alphaTitle" : null,
      |    "hasOffensiveTerm" : false,
      |    "uncensoredTitle" : null
      |  }, {
      |    "titlesetId" : 1127859,
      |    "type" : "AKA",
      |    "language" : "SPANISH",
      |    "text" : "Fútbol Universitario",
      |    "reducedTitles" : [ "Fútbol Univers.", "Fútbol" ],
      |    "alphaTitle" : null,
      |    "hasOffensiveTerm" : false,
      |    "uncensoredTitle" : null
      |  }, {
      |    "titlesetId" : 4617936,
      |    "type" : "AKA",
      |    "language" : "PORTUGUESE",
      |    "text" : "Futebol Universitário",
      |    "reducedTitles" : [ "Futebol Universitário", "Futebol Univer", "Fut Univer" ],
      |    "alphaTitle" : null,
      |    "hasOffensiveTerm" : false,
      |    "uncensoredTitle" : null
      |  } ],
      |  "episodicTitles" : [ {
      |    "titlesetId" : 5176712,
      |    "type" : "MAIN",
      |    "language" : "ENGLISH",
      |    "text" : "ACC Tournament, Final",
      |    "reducedTitles" : [ ],
      |    "alphaTitle" : null,
      |    "hasOffensiveTerm" : false,
      |    "uncensoredTitle" : null
      |  } ],
      |  "qualityRatings" : [ ],
      |  "year" : null,
      |  "sportEvent" : {
      |    "gameNumber" : null,
      |    "playDate" : "2014-11-16",
      |    "playTime" : "14:00:00",
      |    "team1Id" : 4381,
      |    "team2Id" : 4789,
      |    "team1" : "Clemson",
      |    "team1LanguageTag" : "ENGLISH",
      |    "team2" : "Louisville",
      |    "team2LanguageTag" : "ENGLISH",
      |    "productionLocationType" : null,
      |    "locationType" : "NEUTRAL",
      |    "locationTypeLanguageTag" : "ENGLISH",
      |    "locationTypeNames" : [ {
      |      "versionMappingId" : 16985583,
      |      "locationTypeTag" : "NEUTRAL",
      |      "tmsId" : "25270637",
      |      "productionLocationType" : "contra"
      |    }, {
      |      "versionMappingId" : 16985582,
      |      "locationTypeTag" : "NEUTRAL",
      |      "tmsId" : "25270636",
      |      "productionLocationType" : "vs."
      |    }, {
      |      "versionMappingId" : 16985581,
      |      "locationTypeTag" : "NEUTRAL",
      |      "tmsId" : "EP017861790210",
      |      "productionLocationType" : "contra"
      |    }, {
      |      "versionMappingId" : 16985580,
      |      "locationTypeTag" : "NEUTRAL",
      |      "tmsId" : "EP0206851003",
      |      "productionLocationType" : "vs."
      |    } ],
      |    "ifNecessary" : false,
      |    "venueId" : null,
      |    "venueName" : null,
      |    "venueLanguageTag" : null,
      |    "timezone" : "EASTERN OBSERVING",
      |    "season" : {
      |      "seasonId" : 1202,
      |      "seasonType" : "POSTSEASON",
      |      "startYear" : 2014,
      |      "endYear" : 2014
      |    },
      |    "live" : null,
      |    "playoff" : true,
      |    "playoffId" : null,
      |    "playoffRoundName" : null
      |  },
      |  "doNotUse" : false,
      |  "seasonYear" : null,
      |  "seasonNumber" : null,
      |  "parentSeasonId" : null,
      |  "parentSeasonProgramId" : null,
      |  "seasonId" : null,
      |  "episodeNumber" : null,
      |  "regionalSeasonEpisodeNumbers" : null,
      |  "productionStatus" : null,
      |  "targetAudience" : null,
      |  "partNumber" : null,
      |  "numberOfParts" : null,
      |  "productionCode" : null,
      |  "silent" : null,
      |  "filmLocations" : null,
      |  "nolaCode" : null,
      |  "finaleDate" : null,
      |  "pbsnwQual" : false,
      |  "custom" : false,
      |  "sportSeasons" : null,
      |  "programAwardNominations" : null,
      |  "creditAwardNominations" : null,
      |  "latestUpdateTime" : "2014-11-18T09:06:30.553-05:00",
      |  "loadTime" : "2019-09-27T09:16:17.077-04:00",
      |  "protect" : false,
      |  "images" : null,
      |  "trueDuration" : null,
      |  "trueDurationMs" : null,
      |  "official" : null,
      |  "organization" : null,
      |  "keywords" : null,
      |  "owner" : null
      |}
      |""".stripMargin
  }

}
