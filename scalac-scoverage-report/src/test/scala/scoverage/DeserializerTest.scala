package scoverage

import org.scalatest.{OneInstancePerTest, FunSuite}
import scoverage.report.Deserializer

class DeserializerTest extends FunSuite with OneInstancePerTest {

  test("coverage should be deserializable from xml") {
    val input = <statements>
      <statement>
        <source>mysource</source> <package>org.scoverage</package> <class>test</class> <classType>Trait</classType> <fullClassName>org.scoverage.test</fullClassName> <method>mymethod</method> <path>mypath</path> <id>14</id> <start>100</start> <end>200</end> <line>4</line> <description>def test : String</description> <symbolName>test</symbolName> <treeName>DefDef</treeName> <branch>true</branch> <count>32</count> <ignored>false</ignored>
      </statement>
    </statements>
    val statements = List(Statement(
      "mysource",
      Location("org.scoverage", "test", "org.scoverage.test", ClassType.Trait, "mymethod", "mypath"),
      14, 100, 200, 4, "def test : String", "test", "DefDef", true, 32
    ))
    val coverage = Deserializer.deserialize(input.toString())
    assert(statements === coverage.statements.toList)
  }
}
