package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BranchOp {
  this: Obj =>
  def branch[O <: Obj](branches: Obj): O = BranchOp(branches).exec(this)
}

object BranchOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branches: Obj): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.branch, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: Strm[_] => start.via(start, inst)
      case _ => Inst.oldInst(inst).arg0[Poly[Obj]] match {
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case alst: Lst[Obj] => alst.gsep match {
          case Tokens.`,` =>
            val result: List[Obj] = alst.g._2.map(b => Inst.resolveArg(start, b)).filter(_.alive)
            val apoly: Lst[Obj] = alst.clone(g = (alst.gsep, result))
            apoly match {
              case _: Value[_] => strm(result.map(x => x.hardQ(multQ(x.q, inst.q))).filter(_.alive))
              case _: Type[_] =>
                if (result.isEmpty) zeroObj
                else if (1 == result.size) if(result.head.alive) result.head.hardQ(multQ(result.head.q, inst.q)) else zeroObj
                else if (__.isAnonRoot(start) && result.map(x => x.hardQ(qOne)).toSet.size == 1 && result.forall(x => x.root))
                  Option(result.head.hardQ(multQ(result.foldLeft(qZero)((a, b) => plusQ(a, b.q)), inst.q))).filter(_.alive).getOrElse(zeroObj)
                else BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`;` =>
            var running = start
            val result = alst.g._2.map(b => {
              running = running match {
                case astrm: Strm[_] => strm(astrm.values.map(r => Inst.resolveArg(r, b)))
                case r if b.isInstanceOf[Value[_]] => b.hardQ(multQ(r.q, b.q)) // TODO: hardcoded hack -- should really be part of Inst.resolveArg() and Obj.compute()
                case r: Type[_] if r.root && b.root && r.name == b.name => b.hardQ(multQ(r.q, b.q))
                case _ => Inst.resolveArg(running, b)
              }
              running
            })
            val apoly = alst.clone(g = (alst.gsep, result))
            if (result.exists(b => !b.alive)) zeroObj
            else if (result.forall(x => x.root)) result.last.hardQ(multQ(result.last.q, inst.q))
            else apoly match {
              case _: Value[_] => result.last.hardQ(multQ(result.last.q, inst.q))
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`|` =>
            val result: List[Obj] = alst.g._2.map(b => Inst.resolveArg(start, b)).filter(_.alive)
            val apoly: Lst[Obj] = alst.clone(g = (alst.gsep, result))
            apoly match {
              case _: Value[_] => result.find(b => b.alive).map(x => x.hardQ(multQ(x.q, inst.q))).getOrElse(zeroObj)
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
        }
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case arec: Rec[Obj, Obj] => arec.gsep match {
          case Tokens.`,` =>
            val result = arec.g._2.map(b => {
              val key = Inst.resolveArg(start, b._1)
              key -> (if (key.alive) Inst.resolveArg(start, b._2) else zeroObj)
            }).filter(b => b._1.alive).toMap
            val apoly = arec.clone(g = (arec.gsep, result))
            apoly match {
              case _: Value[_] => strm(result.map(x => x._2.hardQ(multQ(x._2.q, inst.q))).toList.asInstanceOf[List[Obj]])
              case _: Type[_] => if (1 == result.size) result.head._2.hardQ(multQ(result.head._2.q, inst.q)) else BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`;` => {
            var running = start
            val result = arec.g._2.map(b => {
              running = running match {
                case astrm: Strm[_] => strm(astrm.values.map(r => if (Inst.resolveArg(r, b._1).alive) Inst.resolveArg(r, b._2) else zeroObj))
                case r if b._2.isInstanceOf[Value[_]] => if (Inst.resolveArg(r, b._1).alive) b._2.hardQ(multQ(r.q, b._2.q)) else zeroObj // TODO: hardcoded hack -- should really be part of Inst.resolveArg() and Obj.compute()
                case r: Type[_] if r.root && b._2.root && r.name == b._2.name => if (Inst.resolveArg(r, b._1).alive) b._2.hardQ(multQ(r.q, b._2.q)) else zeroObj
                case _ => if (Inst.resolveArg(running, b._1).alive) Inst.resolveArg(running, b._2) else zeroObj
              }
              Inst.resolveArg(running, b._1) -> running
            }).filter(b => b._1.alive && b._2.alive).toMap
            if (result.isEmpty) zeroObj
            val apoly = arec.clone(g = (arec.gsep, result))
            apoly match {
              case _: Value[_] => apoly.g._2.last._2
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }

          }
          case Tokens.`|` =>
            val result: List[List[Obj]] = arec.g._2.map(b => {
              val key = Inst.resolveArg(start, b._1)
              List(key, (if (key.alive) Inst.resolveArg(start, b._2) else zeroObj))
            }).foldLeft(List.empty[List[Obj]])((a, b) => a :+ b)
            val apoly = arec.clone(g = (arec.gsep, result.map(x => x.head -> x.tail.head).toMap))
            apoly match {
              case _: Value[_] => result.find(b => b.head.alive).map(b => b.tail.head).getOrElse(zeroObj)
              case _: Type[_] =>
                if (result.size == 1)
                  result.head.tail.head.hardQ(multQ(result.head.tail.head.q, inst.q))
                else
                  BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
        }
      }
    }
  }
}
