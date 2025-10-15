package com.rms.miu.slickcats

import slick.basic.BasicBackend

package object compat {
  // Slick 3.3.x: underlying type is BasicBackend#DatabaseDef
  type CompatDatabaseDef = BasicBackend#DatabaseDef
}
