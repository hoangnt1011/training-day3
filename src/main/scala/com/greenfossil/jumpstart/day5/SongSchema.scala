package com.greenfossil.jumpstart.day5

import com.greenfossil.sqlview2.*

import java.time.LocalDate

object SongSchema extends Schema:

  object SONG_TABLE extends Table:
    override val tablename: String = "SONG_TABLE"
    val id = Column[Long]("id", AutoIncr, NotNullable())
    val title = Column[String]("title", NotNullable())
    val artist = Column[String]("artist", NotNullable())
    val year = Column[Int]("year")
    val json = Column[String]("json", 1000)
    override val tableconstraints: List[TableConstraint] = List(
      PrimaryKey(id)
    )
