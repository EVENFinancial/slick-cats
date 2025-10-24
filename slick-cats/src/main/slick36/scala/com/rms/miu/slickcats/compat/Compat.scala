package com.rms.miu.slickcats

import slick.basic.BasicBackend

package object compat {
  // Slick 3.5.x: underlying type renamed to BasicDatabaseDef
  type CompatDatabaseDef = BasicBackend#BasicDatabaseDef
}

