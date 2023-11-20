package com.greenfossil.jumpstart.day5

import com.greenfossil.sqlview2.*
import com.greenfossil.jumpstart.day5.SongSchema.*

import java.sql.Connection
import scala.util.Try

object SongDAO:

  /**
   * Queries the first song that matches the where clause
   */
  def findSong(whereFn: SONG_TABLE.type => WhereClause)(using connection: Connection): Try[Song] = {
    val select1: SelectStatement = SONG_TABLE.select(s => (s.id, s.title, s.artist, s.year)).where(whereFn(SONG_TABLE))
    select1.executeFirst[Song]
  }

  /**
   * Queries `Song.json` for the value of the `img_url` field
   */
  def findImageUrlOfSong(whereFn: SONG_TABLE.type => WhereClause)(using connection: Connection): Try[String] = {
    Select(Json_Unquote(Json_Extract(SONG_TABLE.json, "$.img_url")))
      .from(SONG_TABLE)
      .where(whereFn(SONG_TABLE))
      .executeFirst[String]
  }

  /**
   * Insert record into the Song table
   * @return ID of the new song record
   */
  def createSong(song: String, artist: String, year: Int)(using connection: Connection): Try[Long] = {
    SONG_TABLE.executeInsertValuesAndReturnGeneratedKey(s => (s.title, s.artist, s.year))(song, artist, year)
  }

  /**
   * Update the row where the song ID matches
   * @return Num of updated rows
   */
  def updateSong(songId: Long, song: String, artist: String, year: Int)(using connection: Connection): Try[Int] = {
    val updateClause = SONG_TABLE.update(s => (s.title, s.artist, s.year)).setValues(song, artist, year).where(s => where"${s.id} = ${songId}")
    updateClause.executeUpdate
  }

  /**
   * Delete the row where the song ID matches
   * @return Num of updated rows
   */
  def deleteSong(songId: Long)(using connection: Connection): Try[Int] = {
    val deleteClause = SONG_TABLE.deleteWhere(s => where"${s.id} = ${songId}")
    deleteClause.executeUpdate
  }

