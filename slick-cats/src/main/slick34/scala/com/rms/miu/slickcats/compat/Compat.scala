package com.rms.miu.slickcats

import slick.basic.BasicBackend

package object compat {
  // Slick 3.4.x: underlying type is still BasicBackend#DatabaseDef
  type CompatDatabaseDef = BasicBackend#DatabaseDef
}

