package scala.cli

import org.scalatest.FlatSpec

class ArgsParserSpec extends FlatSpec {

  behavior of "ArgsParser"

  it should "parse binary args" in {
    val input = "--key value"
    val args = ArgsParser(input)
    assert(args.required("key") == "value")
  }

  it should "parse unary args" in {
    val input = "--key"
    val args = ArgsParser(input)
    assert(args.boolean("key"))
    intercept[IllegalArgumentException] {
      args.required("key")
    }
  }

  it should "parse unary and binary args in the same input string" in {
    val input = "--key1 --key2 value"
    val args = ArgsParser(input)
    assert(args.boolean("key1"))
    assert(args.required("key2") == "value")
  }
}
