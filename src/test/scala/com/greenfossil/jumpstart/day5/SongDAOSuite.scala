package com.greenfossil.jumpstart.day5

import com.greenfossil.commons.json.{JsObject, Json}
import com.greenfossil.sqlview2.util.MysqlDBSupport
import com.greenfossil.sqlview2.*
import munit.FunSuite

class SongDAOSuite extends FunSuite, MysqlDBSupport:
  override def dbname: String = "testmusicschema"

  override def dbpassword = "aA123456"

  test("SongDAO.findSong"){
    createReadOnlyForTest{ implicit connection =>
      SongDAO.findSong(t => where"${t.title} = 'The Mother We Share'").fold(
        ex => fail("Should not return a failure", ex),
        song =>
          assertNoDiff(song.title, "The Mother We Share")
          assertNoDiff(song.artist, "Chvrches")
          assertEquals(song.year, 2013)
      )

      assert(SongDAO.findSong(t => where"${t.title} = 'The Mother We Share' AND ${t.year} = 1990").isFailure)
    }
  }

  test("SongDAO.findImageUrlOfSong"){
    createReadOnlyForTest { implicit connection =>
      SongDAO.findImageUrlOfSong(t => where"${t.title} = 'Shelter from the Storm'").fold(
        ex => fail("Should not return a failure", ex),
        imageUrl => assertNoDiff(imageUrl, "http://fireflygrove.com/songnotes/images/artists/BobDylan.jpg")
      )
    }
  }

  test("SongDAO.createSong"){
    createLocalTxForTest { implicit connection =>
      SongDAO.createSong("Livin' On A Prayer", "Bon Jovi", 1986).fold(
        ex => fail("Failed to create song", ex),
        songId =>
          SongDAO.findSong(t => where"${t.id} = $songId").fold(
            ex => fail("Failed to retrieve newly created song", ex),
            song =>
              assertNoDiff(song.title, "Livin' On A Prayer")
              assertNoDiff(song.artist, "Bon Jovi")
              assertEquals(song.year, 1986)
          )
      )
    }
  }

  test("SongDAO.updateSong"){
    createLocalTxForTest { implicit connection =>
      val result = for {
        song <- SongDAO.findSong(t => where"${t.title} = 'The Weight'")
        nRows <- SongDAO.updateSong(song.id, "The Summer", "The Bands", 1967)
        _ = assertEquals(nRows, 1)
        updatedSong <- SongDAO.findSong(t => where"${t.id} = ${song.id}")
        _ = assertNoDiff(updatedSong.title, "The Summer")
        _ = assertNoDiff(updatedSong.artist, "The Bands")
        _ = assertEquals(updatedSong.year, 1967)
      } yield updatedSong

      result.fold(
        ex => fail("Failed to update song", ex),
        identity
      )
    }
  }

  test("SongDAO.deleteSong"){
    createLocalTxForTest { implicit connection =>
      val result = for {
        song <- SongDAO.findSong(t => where"${t.title} = 'The Weight'")
        nRows <- SongDAO.deleteSong(song.id)
        _ = assertEquals(nRows, 1)
        _ = assert(SongDAO.findSong(t => where"${t.id} = ${song.id}").isFailure)
      } yield nRows

      result.fold(
        ex => fail("Failed to delete song", ex),
        identity
      )
    }
  }

  override def beforeAll(): Unit =
    createDatabase(true)
    createLocalTxForTest{ implicit connection =>
      SongSchema.recreateAllTables()

      val is = this.getClass.getClassLoader.getResourceAsStream("json/songs.json")
      val songJsArr = Json.parse(is).as[Seq[JsObject]]
      val songValues = for {
        songJsObj <- songJsArr
        title <- (songJsObj \ "title").asOpt[String]
        artist <- (songJsObj \ "artist").asOpt[String]
        year <- (songJsObj \ "year").asOpt[Int]
      } yield (title, artist, year, songJsObj.stringify)

      import SongSchema.SONG_TABLE
      SONG_TABLE.insertValues(t => (t.title, t.artist, t.year, t.json))(songValues).executeUpdate
        .fold(ex => fail("Failed to setup", ex), _ => println("Setup successfully"))
    }


