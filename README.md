# Argumentation

This is about [argumentation](https://dstl.github.io/eleatics/doc/argumentation/).

It includes an implementation of backtracking algorithms<sup> [1]</sup> that operate on Dung's _argumentation framework_<sup> [2]</sup> model of the acceptability of arguments.

We want to reason about _argument maps_ expressed as Argument Interchange Format (AIF)<sup> [3]</sup>, so we need a way to translate an AIF argument map to a Dung argumentation framework (DAF) first in order to evaluate it. We use the _presumptive arguments_ method<sup> [4]</sup>.

Argument maps and frameworks are *graphs*. We use **Dover**<sup> [5]</sup> for modelling and manipulating graphs.

### References 

1.	Samer Nofal, Katie Atkinson, Paul E. Dunne. 
"Looking-ahead in backtracking algorithms for abstract argumentation",
_International Journal of Approximate Reasoning_, Volume 78, 2016. Pages 265-282, ISSN 0888-613X,
DOI: [10.1016/j.ijar.2016.07.013](https://doi.org/10.1016/j.ijar.2016.07.013).

1.  Dung, Phan Minh. "On the acceptability of arguments and its fundamental role in nonmonotonic reasoning, logic programming and n-person games." _Artificial intelligence_ 77.2 (1995): 321-357. DOI: [10.1016/0004-3702(94)00041-X](https://doi.org/10.1016/0004-3702%2894%2900041-X)

1. See: [Contributing to the Argument Interchange Format](http://www.arg.tech/index.php/research/contributing-to-the-argument-interchange-format/), ARG-tech

1. Knox, A., "Explaining presumptive arguments", _Dstl Technical Memo_ (2022), [https://dstl.github.io/eleatics/argumentation/explain-framework.pdf](https://dstl.github.io/eleatics/argumentation/explain-framework.pdf)

1. Baker, Rob, Alena Frankel, and Peter Rodgers. "Dover: Scalable Algorithms for Graph Mining in Java." (2017). See: [https://www.cs.kent.ac.uk/projects/dover/](https://www.cs.kent.ac.uk/projects/dover/)