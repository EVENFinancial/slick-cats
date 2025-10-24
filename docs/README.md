SlickCats (Fork)
================

**Note**: This is a fork of the original [SlickCats](https://github.com/RMSone/slick-cats) project with updated dependencies and extended compatibility matrix.

[Cats](https://github.com/typelevel/cats) instances for [Slick's](http://slick.typesafe.com/) `DBIO` including:
* Monad
* MonadError
* CoflatMap
* Group
* Monoid
* Semigroup
* Comonad
* Order
* PartialOrder
* Equals

## Using

This fork provides separate artifacts for different Slick versions to ensure compatibility. Add the appropriate dependency to your build definition:

```scala
// For Slick 3.3.x
libraryDependencies += "tech.engine" %% "slickcats-slick3-3" % "0.11.0-SNAPSHOT" // Scala 2.12/2.13

// For Slick 3.4.x
libraryDependencies += "tech.engine" %% "slickcats-slick3-4" % "0.11.0-SNAPSHOT" // Scala 2.12/2.13

// For Slick 3.5.x
libraryDependencies += "tech.engine" %% "slickcats-slick3-5" % "0.11.0-SNAPSHOT" // Scala 2.12/2.13/3.3.x (LTS)

// For Slick 3.6.x
libraryDependencies += "tech.engine" %% "slickcats-slick3-6" % "0.11.0-SNAPSHOT" // Scala 2.12/2.13/3.3.x (LTS)
```

Because of possible binary incompatibilities, here are the dependency versions used in each release:

This fork supports multiple Slick and Scala version combinations:

| Artifact             | Release              | Slick Version |     Scala Versions      | Cats Version |
|:---------------------|:--------------------:|:-------------:|:-----------------------:|:------------:|
| `slickcats-slick3-3` | 0.11.0-SNAPSHOT      |     3.3.3     |    2.12.19, 2.13.16     |    2.13.0    |
| `slickcats-slick3-4` | 0.11.0-SNAPSHOT      |     3.4.1     |    2.12.19, 2.13.16     |    2.13.0    |
| `slickcats-slick3-5` | 0.11.0-SNAPSHOT      |     3.5.2     | 2.12.19, 2.13.16, 3.3.6 |    2.13.0    |
| `slickcats-slick3-6` | 0.11.0-SNAPSHOT      |     3.6.1     | 2.12.19, 2.13.16, 3.3.6 |    2.13.0    |

Artifacts are publicly available on Maven Central starting from version *0.6*.

## Accessing the Instances
Some or all of the following imports may be needed:
```scala mdoc:silent
import cats._
import slick.dbio._
import com.rms.miu.slickcats.DBIOInstances._
```
Additionally, be sure to have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non-obvious errors if it's missing.
```scala mdoc:fail
implicitly[Monad[DBIO]]
```

```scala mdoc:silent
import scala.concurrent.ExecutionContext.Implicits.global
```

instances will be available for:
```scala mdoc:silent
implicitly[Monad[DBIO]]
implicitly[MonadError[DBIO, Throwable]]
implicitly[CoflatMap[DBIO]]
implicitly[Functor[DBIO]]
implicitly[Applicative[DBIO]]
```

If a Monoid exists for `A`, here taken as Int, then the following is also available
```scala mdoc:silent
implicitly[Group[DBIO[Int]]]
implicitly[Semigroup[DBIO[Int]]]
implicitly[Monoid[DBIO[Int]]]
```

## Known Issues
Instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```scala mdoc
def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa

val fail1: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello")
val fail2 = DBIO.successful("hello")
val success: DBIO[String] = DBIO.successful("hello")
```
will _not_ compile
```scala mdoc:fail
monad(fail1)
monad(fail2)
```
but
```scala mdoc
monad(success)
```
will compile fine.

## Extras
This README is compiled using [mdoc](https://scalameta.org/mdoc/) to ensure that only working examples are given.
