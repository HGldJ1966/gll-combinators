package edu.uwm.cs.util

private[cs] object SetSyntax {
  implicit def setSyntax[A](set: Set[A]): RichSet[A] = new RichSet(set)
  
  class RichSet[A](set: Set[A]) {
    def isComplement = set.isInstanceOf[ComplementarySet[_]]
    
    def complement = new ComplementarySet(set)
  }
}
