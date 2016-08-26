# collatz4j
## A visualization of the Collatz Conjecture

The [Collatz conjecture](https://en.wikipedia.org/wiki/Collatz_conjecture), also known as the Hailstone Conjecture, starts with what appears to be a silly math 'game'. 
Given any positive whole number you use the following two rules

* if the number is even, divide it by 2
* if the number is odd, multiply by 3 and add 1

With the resultant number repeat the process. The number may bounce around going higher or lower, but the Collatz Conjecture states that
eventually *all* numbers will end up at 1. This has been tested up to numbers as high as 2^60. There is no proof, however, and Paul Erdős said about the Collatz conjecture: "Mathematics may not be ready for such problems." He offered $500 for its solution.

One usually draws graphs of these chains such as


    >-----------v
    |           |
    1 <-- 2 <-- 4 <-- 8 <-- 16 <-- 32
                             ^
                             |
                             5 <-- 10 <-- 3 <-- 6 <-- 12
                                   ^
                                   |
                                   20 <-- 40
                                          ^
                                          |
                                          13 <-- 26 <-- 52 <-- 104
                                                        ^
                                                        |
                                                        17 <-- 34 <-- 68
                                                               ^
                                                               |
                                                               11 <-- 22 <--44
                                                                      ^
                                                                      |
                                                                      7 <-- 14 <-- 28 <--56
                                                                                   ^
                                                                                   |
                                                                                   9 <-- 18
                         
As you can imagine as the numbers get larger, the 'limbs' of the tree can get quite long.

This program attempts to draw a 3D visualization of this graph.

For a good overview of the Collatz Conjecture, watch this [video](https://www.youtube.com/watch?v=5mFpVDpKX70).
