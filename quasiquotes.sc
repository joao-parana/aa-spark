// Scala’s metaprogramming support happens at compile time using a macro facility. Macros
// work more like constrained compiler plug-ins, because they manipulate the abstract
// syntax tree (AST) produced from the parsed source code. Macros are invoked to manipulate
// the AST before the final compilation phases leading to byte-code generation.
import scala.reflect.runtime.universe._ 
val C = q"class C"
println(showCode(C))
println(showRaw(q"class C")) 
// Quasiquotes are a neat notation that lets you manipulate Scala syntax trees with ease:
val tree = q"i am { a quasiquote }"
//
// Every time you wrap a snippet of code in q"..." it will become a tree that represents a given snippet. 
// As you might have already noticed, quotation syntax is just another usage of extensible string 
// interpolation, introduced in 2.10. Although they look like strings they operate on syntactic trees
// under the hood.
//
// The same syntax can be used to match trees as patterns:
println(tree match { case q"i am { a quasiquote }" => "it worked!" })
//
// Whenever you match a tree with a quasiquote it will match whenever the structure of a given tree
// is equivalent to the one you've provided as a pattern. You can check for structural equality
// manually with the help of equalsStructure method:
println(q"foo + bar" equalsStructure q"foo.+(bar)")
//
// You can also put things into quasiquotation with the help of $:
val aquasiquote = q"a quasiquote"
val tree = q"i am { $aquasiquote }"
//
// This operation is also known as unquoting. Whenever you unquote an expression of type Tree in a quasiquote it will structurally substitute that tree into that location. Most of the time such substitutions between quotes is equivalent to a textual substitution of the source code.
//
// Similarly, one can structurally deconstruct a tree using unquoting in pattern matching:
//
// Interpolators
// 
// Each of these contexts is covered by a separate interpolator:
//
//  	Used for
// q	expressions, definitions and imports
// tq	types
// pq	patterns
// cq	case clause
// fq	for loop enumerator
// 
// Offline code generation
//
// We can implement an offline code generator that does AST manipulation with the help of quasiquotes,
// and then serializes that into actual source code right before writing it to disk:
object OfflineCodeGen extends App {
    def generateCode() =
      q"package mypackage { class MyClass }"
    def saveToFile(path: String, code: Tree) = {
      val writer = new java.io.PrintWriter(path)
      try writer.write(showCode(code))
      finally writer.close()
    }
    saveToFile("myfile.scala", generateCode())
  }
OfflineCodeGen.main(null) // salva o fonte myfile.scala
//
// Just in time compilation
//
// Thanks to the ToolBox API, one can generate, compile and run Scala code at runtime:
//
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
val toolbox = currentMirror.mkToolBox()
// 
val code = q"""
println("compiled and run at runtime!")
println("that's all folks!")
"""
val compiledAndRunCode = toolbox.compile(code)
val result = compiledAndRunCode()

val codeStr = """
println("compiled and run at runtime!")
println("that's all folks!")
"""
val code = q"$codeStr"
val compiledAndRunCode = toolbox.compile(code)
val result = compiledAndRunCode()
// 
// usando ToolBoxFactory
val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
showRaw(tb.parse("println(2)"))
val tree = tb.parse(codeStr) 
toolbox.eval(tree)
val otherCodeStr = """
println("compiled and run at " + "runtime!")
println("that's all folks!")
"""
val tree = tb.parse(otherCodeStr)
tb.typeCheck(tree)
toolbox.eval(tree)
val yetAnotherCodeStr = """
println("compiled and run at " - "runtime!")
println("that's all folks!")
"""
val tree = tb.parse(yetAnotherCodeStr)
tb.typeCheck(tree)
// scala.tools.reflect.ToolBoxError: reflective compilation has failed: value - is not a member of String
toolbox.eval(tree)
// scala.tools.reflect.ToolBoxError: reflective compilation has failed: value - is not a member of String
