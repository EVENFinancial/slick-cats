SlickCats (Fork)
================


> **Note**: This is a fork of the original [SlickCats](https://github.com/RMSone/slick-cats) project with updated dependencies and extended compatibility matrix.

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
libraryDependencies += "com.rms.miu" %% "slickcats-slick3-3" % "0.11.0" // Scala 2.12/2.13

// For Slick 3.4.x  
libraryDependencies += "com.rms.miu" %% "slickcats-slick3-4" % "0.11.0" // Scala 2.12/2.13

// For Slick 3.5.x
libraryDependencies += "com.rms.miu" %% "slickcats-slick3-5" % "0.11.0" // Scala 2.12/2.13/3.3.x (LTS)
```

### Compatibility Matrix

This fork supports multiple Slick and Scala version combinations:

| Artifact | Slick Version | Scala Versions | Cats Version |
|:---------|:-------------:|:--------------:|:------------:|
| `slickcats-slick3-3` | 3.3.3 | 2.12.19, 2.13.16 | 2.13.0 |
| `slickcats-slick3-4` | 3.4.1 | 2.12.19, 2.13.16 | 2.13.0 |
| `slickcats-slick3-5` | 3.5.2 | 2.12.19, 2.13.16, 3.3.6 | 2.13.0 |

### Publishing

Currently published to GitHub Packages. Maven Central publishing is planned for future releases.

To use GitHub Packages, add the following resolver to your `build.sbt`:

```scala
resolvers += "GitHub Package Registry" at "https://maven.pkg.github.com/EVENFinancial/slick-cats"
```

You'll also need to authenticate with GitHub Packages. See [GitHub's documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) for setup instructions.

## Accessing the Instances

Some or all of the following imports may be needed:

```scala
import cats._
import slick.dbio._
import com.rms.miu.slickcats.DBIOInstances._
```

Additionally, be sure to have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non-obvious errors if it's missing.

```scala
implicitly[Monad[DBIO]]
// error: could not find implicit value for parameter e: cats.Monad[slick.dbio.DBIO]
// implicitly[Monad[DBIO]]
// ^^^^^^^^^^^^^^^^^^^^^^^
```

```scala
import scala.concurrent.ExecutionContext.Implicits.global
```

instances will be available for:

```scala
implicitly[Monad[DBIO]]
implicitly[MonadError[DBIO, Throwable]]
implicitly[CoflatMap[DBIO]]
implicitly[Functor[DBIO]]
implicitly[Applicative[DBIO]]
```

If a Monoid exists for `A`, here taken as Int, then the following is also available

```scala
implicitly[Group[DBIO[Int]]]
implicitly[Semigroup[DBIO[Int]]]
implicitly[Monoid[DBIO[Int]]]
```

## Known Issues

Instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```scala
def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa

val fail1: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello")
// fail1: DBIOAction[String, NoStream, Effect.All] = SuccessAction("hello")
val fail2 = DBIO.successful("hello")
// fail2: DBIOAction[String, NoStream, Effect] = SuccessAction("hello")
val success: DBIO[String] = DBIO.successful("hello")
// success: DBIO[String] = SuccessAction("hello")
```

will _not_ compile

```scala
monad(fail1)
monad(fail2)
// error: inferred kinds of the type arguments ([-E <: slick.dbio.Effect]slick.dbio.DBIOAction[String,slick.dbio.NoStream,E],slick.dbio.Effect.All) do not conform to the expected kinds of the type parameters (type F,type A).
// [-E <: slick.dbio.Effect]slick.dbio.DBIOAction[String,slick.dbio.NoStream,E]'s type parameters do not match type F's expected parameters:
// type E's bounds <: slick.dbio.Effect are stricter than type _'s declared bounds >: Nothing <: Any
// monad(fail1)
// ^^^^^
// error: type mismatch;
//  found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect.All]
//  required: F[A]
// monad(fail1)
//       ^^^^^
// error: inferred kinds of the type arguments ([-E <: slick.dbio.Effect]slick.dbio.DBIOAction[String,slick.dbio.NoStream,E],slick.dbio.Effect) do not conform to the expected kinds of the type parameters (type F,type A).
// [-E <: slick.dbio.Effect]slick.dbio.DBIOAction[String,slick.dbio.NoStream,E]'s type parameters do not match type F's expected parameters:
// type E's bounds <: slick.dbio.Effect are stricter than type _'s declared bounds >: Nothing <: Any
// monad(fail2)
// ^^^^^
// error: type mismatch;
//  found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect]
//  required: F[A]
// monad(fail2)
//       ^^^^^
```

but

```scala
monad(success)
// res10: DBIO[String] = SuccessAction("hello")
```

will compile fine.

## Fork Changes

This fork includes the following improvements over the original:

- **Extended Compatibility**: Support for Slick 3.3.x, 3.4.x, and 3.5.x
- **Scala 3 Support**: Full compatibility with Scala 3.3.6 LTS
- **Updated Dependencies**: Latest Cats 2.13.0 and modern Scala versions
- **Matrix Build**: Separate artifacts for each Slick/Scala combination
- **Comprehensive Testing**: Property-based testing with Cats laws verification

## Extras

This README is compiled using [mdoc](https://scalameta.org/mdoc/) to ensure that only working examples are given.
