package edu.uwm.cs.gll

class DisjunctiveParser[A](l: =>Parser[A], r: =>Parser[A]) extends NonTerminalParser[A] with Thunkable {
  private lazy val left = l
  private lazy val right = r
  
  private lazy val leftClass = thunk[Parser[A]]('l).getClass
  private lazy val rightClass = thunk[Parser[A]]('r).getClass
  
  lazy val gather = gatherImpl(Set())
  
  def computeFirst(seen: Set[Parser[Any]]) = {
    lazy val newSeen = seen + this
    
    if (seen contains this)
      Set()
    else
      left.computeFirst(newSeen) ++ right.computeFirst(newSeen)
  }
  
  def queue(t: Trampoline, in: Stream[Char])(f: (A, Stream[Char])=>Unit) {
    var results = Set[(A, Stream[Char])]()    // merge results
    for {
      pre <- gather
      val p = pre.asInstanceOf[Parser[A]]
      
      if (in.isEmpty || p.first.contains(in.head)) || p.first.size == 0     // lookahead
    } p.queue(t, in) { (v, tail) => results += ((v, tail)) }
    
    for ((v, tail) <- results) {
      f(v, tail)
    }
  }
  
  private def gatherImpl(seen: Set[DisjunctiveParser[A]]): Set[Parser[A]] = {
    lazy val newSeen = seen + this
    
    def process(p: Parser[A]): Set[Parser[A]] = p match {
      case d: DisjunctiveParser[A] => {
        if (!seen.contains(d))
          d.gatherImpl(newSeen)
        else
          Set[Parser[A]]()
      }
      
      case p => Set(p)
    }
    
    process(left) ++ process(right)
  }
  
  override def equals(other: Any) = other match {
    case that: DisjunctiveParser[A] => {
      this.leftClass == that.leftClass && this.rightClass == that.rightClass
    }
    
    case _ => false
  }
  
  override def hashCode = leftClass.hashCode + rightClass.hashCode
}
