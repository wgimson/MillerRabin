// William Gimson
// Term Project Part III
// CSc 4520
// Dr. Prasad
// myMillerRabinImplementation.java
import java.math.BigInteger;
import java.util.Random;

public class myMillerRabinImplementation {
    
    // our random number generator    
    private static final Random rnd = new Random();

    // our long ints for measuring run time in milliseconds
     static long startTime, endTime;
    
    // the main function is designed to accept command line arguments- "test" 
    // followed by a number n greater than 3 will test that number for primality
    // - "genprime" followed by a number p will return a prime number of p bits 
    // - make sure to include two command line arguments when running program
    public static void main(String[] args) {
        // if first command line argument is "test", we will send the second 
        // argument to millerRabin to test if prime
        if (args[0].equals("test")) {
            // this is our number of arbitrary precision to test
            BigInteger n = new BigInteger(args[1]);
            // if millerRabin returns true, the number is very likely prime -
            // specifcally a result of prime signifies a cetainty of 1  - (1/(4^20))
            // , for reasons that will become clear below - otherwise the number
            // is ceratinly a composite - also, we begin timing algorithms
            // millerRabin and millerRabinPass here
            startTime = System.currentTimeMillis();
            System.out.println(millerRabin(n) ? "PRIME" : "COMPOSITE");
            // get end time - millerRabin and millerRabinPass have terminated
            endTime = System.currentTimeMillis();
            System.out.println("\nTotal run time for algorithm was: " +
                    (endTime - startTime) + " milliseconds.\n");
        // if the first command line argument is "genprime", we will generate a 
        // prime number the length in bits of the second command line argument
        // p - these are useful in testing the correctness of millerRabinPass
        // and timing the running of millerRabinPass and millerRabin
        } else if (args[0].equals("genprime")) {
            int nbits = Integer.parseInt(args[1]);
            BigInteger p;
        do {
            // set BigInteger p to random number of n bits - we don't know if 
            // p is a prime number yet or not
            p = new BigInteger(nbits, rnd);
            // we use 2 and the first few prime numbers right away to test if
            // p divides evenly into these - if so, p is of course not prime 
            // and we must generate a new BigInteger p
            if (p.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) continue;
            if (p.mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)) continue;
            if (p.mod(BigInteger.valueOf(5)).equals(BigInteger.ZERO)) continue;
            if (p.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) continue;
        // we then pass p to millerRabin for more thorough primality testing -
        // while it returns with false, meaning that p is composite, we generate
        // new random BigInteger p
        } while (!millerRabin(p));
        // print randomly generated prime p of n bit length
        System.out.println(p);
        }
    }
    
    // millerRabin generates random BigIntegers to pass to millerRabinPass, 
    // along with the BigInteger n to be tested for primality, 20 times in
    // a row - in this way we can reduce the likelyhood that a composite was
    // returned as a prime to (1/(4^20)), since for each 
    // millerRabinPass the likelyhood that  a composite is returned as a prime
    // is (1/4) - again, the loop can be extended for greater certainty
    public static boolean millerRabin(BigInteger n) {
        for (int i = 0; i < 20; i++) {
            BigInteger a;
            do {
                // make a a random integer type of equivalent bit length to n
                a = new BigInteger(n.bitLength(), rnd);
            // try again is a is 0
            } while (a.equals(BigInteger.ZERO));
            // during a loop of 20 passes, if any single pass returns false, 
            // n is composite
            if (!millerRabinPass(a, n)) {
                return false;
            }
        }
        // otherwise, n has returned true for all 20 iterations of 
        // millerRabinPass, so we can claim with certainty of 1 - (1/(4^20)) that
        // it is in fact prime - we could easily change the number of iterations
        // through millerRabinPass to either increase or decrease precision
        // - k iterations will make the probability that our returned prime is
        // in fact a prime equal to 1 - (1/4^k)
        return true;
    }
    
    // millerRabinPass performs two tests; i) does a^d (mod n) equal 1
    // and ii) does a^d*i (mod n) equal (n-1) - the latter is performed
    // for values - to (s-1) - should any of these be true, the function 
    // returns true immediately - i.e. n is a prime with certainty (3/4)
    private static boolean millerRabinPass(BigInteger a, BigInteger n) {
        // here is our (n-1) for the modular exponentiation test 
        // a^d = (n-1) (mod n)
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        // (n-1) = 2^s * d; so the power of two represented by the lowest set 
        // bit of nMinusOne (n-1) will be our 2^s - we set d equal to nMinusOne,
        // but in fact d = (n-1)/(2^s) - so all we have to do now is shift d
        // to the right by the amout of the lowest one bit in nMinusOne, which 
        // is equivalent to dividing (n-1) by 2^s, which should give us d
        BigInteger d = nMinusOne;
        int s = d.getLowestSetBit();
        d = d.shiftRight(s);

        // here is our modular exponentiation function - a^d (mod n) - if the 
        // result is 1, i.e. if the remainder of dividing a^d by n is 1, we have 
        // a prime with probability (3/4) - of course this will be repeated 20
        // more times in function millerRabin to increase this probabiltity to
        // 1 - (1/(4^20))
        BigInteger aToPower = a.modPow(d, n);
        if (aToPower.equals(BigInteger.ONE)) return true;
        // if the above fails, that does not necessarily mean we have a 
        // composite - we now proceed to test 2, which is does 
        // (mod n) a^(d^i) (mod n) equal (-1) for all i from 0 to (s-1), or 
        // until one is true, at which point we return to millerRabin- if so,
        // we return n a prime with probability (3/4), and repeat the test 20
        // more times - here some implementations actually use 
        // a^((2^r)*d) (mod n) equal (-1) - I chose to go with the variant that
        // was the most well-documented
        for (int i = 0; i < s-1; i++) {
            if (aToPower.equals(nMinusOne)) return true;
            aToPower = aToPower.multiply(aToPower).mod(n);
        }
        // one more time
        if (aToPower.equals(nMinusOne)) return true;
        return false;
    }
}

