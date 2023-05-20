package Hashing;

import java.util.ArrayList;
import java.util.List;

public class HashN implements PerfectHashing {
    private int N;
    private int b; //number of bits: M = 2^b
    private HashN2[] hashTable;
    private Matrix hashFunction;
    ArrayList<ArrayList<Pair>> SecondLevelTemp;
    private int elementCounter;
    private int rebuildCounter;
    public HashN(int n) {
        int closestPowerOf2 = 1;
        int tmpBits = 0;
        while(closestPowerOf2 < n) {
            closestPowerOf2 <<= 1;
            tmpBits++;
        }
        this.N = closestPowerOf2;
        this.b = tmpBits;
        this.hashTable = new HashN2[this.N];
        this.elementCounter = 0;
        this.rebuildCounter = 0;
        this.SecondLevelTemp=new ArrayList<ArrayList<Pair>>();
        for(int i=0;i<this.N;i++)
            this.SecondLevelTemp.add(new ArrayList<Pair>());
    }

    @Override
    public boolean insert(Pair pair) {
        if(this.elementCounter == 0) {
            this.hashFunction = MatrixRandomGenerator.generate(this.b, 32);
            hashFunction.print();
        }
        int key = pair.key;
        Object value = pair.value;
        int index = calcIndex(key);

        if (this.hashTable[index] == null) {
            this.hashTable[index] = new HashN2(1);
            this.hashTable[index].insert(pair);
            this.SecondLevelTemp.get(index).add(pair);
            this.elementCounter++;
            return true;
        }
        else if (this.hashTable[index].searchForKey(key)){
            System.out.println("Word already exists!");
            return false;
        }
        //if there is a collision in the second level
        else if(hashTable[index].collisionCheck(key)) {
            ArrayList<Pair> temp = hashTable[index].getPairs();
            temp.add(pair);
            this.rebuildCounter++;
            this.hashTable[index] = new HashN2(temp.size());
            this.hashTable[index].batchInsert(temp);
            this.elementCounter++;
            return true;
        }
        else {
            if(this.hashTable[index].insert(pair)){
                this.elementCounter++;
                return true;
            }
            return false;
        }

    }

    @Override
    public int batchInsert(List<Pair> pairs) {
        int counter = 0;
        for(Pair pair : pairs) {
            int key = pair.key;
            Object value = pair.value;
            int index = calcIndex(key);
            this.SecondLevelTemp.get(index).add(pair);
        }
        for(int i=0;i<this.N;i++)
        {
            if(this.SecondLevelTemp.get(i).size()>0)
            {
                this.hashTable[i]=new HashN2(this.SecondLevelTemp.get(i).size());
                counter+=this.hashTable[i].batchInsert(this.SecondLevelTemp.get(i));
                this.SecondLevelTemp.get(i).clear();
            }
            System.out.println("i: "+i+" counter: "+counter);
        }
        return counter;
    }

    @Override
    public boolean delete(Pair pair) {
        int key = pair.key;
        int index = calcIndex(key);
        if(this.hashTable[index] == null) {
            System.out.println("Word doesn't exist!");
            return false;
        }
        else if(this.hashTable[index].searchForKey(key)) {
            this.hashTable[index].delete(pair);
            this.elementCounter--;
            return true;
        }
        else {
            System.out.println("Word doesn't exist!");
            return false;
        }
    }

    @Override
    public int batchDelete(Pair[] pairs) {
        int counter = 0;
        for(Pair pair : pairs) {
            int key = pair.key;
            int index = calcIndex(key);
            if(this.hashTable[index] == null) {
                System.out.println("Word doesn't exist!");
            }
            else if(this.hashTable[index].searchForKey(key)) {
                this.hashTable[index].delete(pair);
                counter++;
            }
            else {
                System.out.println("Word doesn't exist!");
            }
        }
        return counter;
    }

    @Override
    public Object lookup(int key) {
        int index = calcIndex(key);
        if(this.hashTable[index] == null) {
            System.out.println("Word doesn't exist!");
            return null;
        }
        else if(this.hashTable[index].searchForKey(key)) {
            return this.hashTable[index].lookup(key);
        }
        else {
            System.out.println("Word doesn't exist!");
            return null;
        }
    }

    @Override
    public Object[] BatchLookup(int[] keys) {
        Object[] values = new Object[keys.length];
        for(int i = 0; i < keys.length; i++) {
            int key = keys[i];
            int index = calcIndex(key);
            if(this.hashTable[index] == null) {
                System.out.println("Word doesn't exist!");
                values[i] = null;
            }
            else if(this.hashTable[index].searchForKey(key)) {
                values[i] = this.hashTable[index].lookup(key);
            }
            else {
                System.out.println("Word doesn't exist!");
                values[i] = null;
            }
        }
        return values;
    }

    @Override
    public boolean searchForKey(int key) {
        int index = calcIndex(key);
        if(this.hashTable[index] == null) {
            System.out.println("Word doesn't exist!");
            return false;
        }
        else if(this.hashTable[index].searchForKey(key)) {
            return true;
        }
        else {
            System.out.println("Word doesn't exist!");
            return false;
        }
    }

    @Override
    public void rehash() {

    }

    @Override
    public int getRebuildCounter() {
        for(int i = 0; i < this.N; i++) {
            if(this.hashTable[i] != null) {
                this.rebuildCounter += this.hashTable[i].getRebuildCounter();
            }
        }
        return this.rebuildCounter;
    }

    @Override
    public void print() {
        for(int i = 0; i < this.N; i++) {
            if(this.hashTable[i] != null) {
                System.out.println("first level index: " + i);
                this.hashTable[i].print();
            }
        }
    }
    private int calcIndex(int key) {
        Matrix keyMatrix = Matrix.convertToMatrix(key);
        Matrix indexMatrix = this.hashFunction.multiply(keyMatrix);
        return Matrix.convertMatrixToIndex(indexMatrix);
    }

}
