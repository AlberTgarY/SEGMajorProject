/**
 * Represents a slice of an array by reference.
 * Use to avoid copying elements and creating new arrays.
 */
export default class ArraySlice {
  /**
   * The array that has been sliced into.
   * 
   * @type {*[]}
   */
  #array;
  /**
   * The beginning of the specified portion of the array.
   * 
   * @type {number}
   */
  #from;
  /**
   * The length of the slice.
   * 
   * @type {number}
   */
  #length;

  /**
   * Creates an instance of ArraySlice.
   * 
   * @param {*[]} array The array to slice into
   * @param {number} from The beginning of the specified portion of the array
   * @param {number} maxLength The maximum length of the slice
   */
  constructor(array, from, maxLength) {
    if (!(array instanceof Array)) throw new Error("array must be array");
    if (maxLength < 0) throw new Error("maxLength cannot be negative");
    this.#array = array;
    if (array.length === 0) {
      this.#from = -1;
      this.#length = 0;
    } else {
      if (from < 0 || from >= array.length) throw new Error("from out of bounds");
      this.#from = from;
      this.#length = Math.min(maxLength, array.length - from);
    }
  }

  /**
   * Accesses the item in the array slice at the given index.
   *
   * @param {number} i The index to access
   * @returns The value at the given index in the array slice
   */
  get(i) {
    if (i < 0 || i >= this.length) throw new Error("Index out of bounds");
    return this.#array[i + this.#from];
  }

  /**
   * Gets the length of the array slice.
   *
   * @type {number} 
   * @readonly
   */
  get length() {
    return this.#length;
  }

  /**
   * Gets an iterable for the array slice.
   *
   * @returns {Iterable.<*>} An iterable over this array slice
   */
  [Symbol.iterator]() {
    let index = this.#from - 1;
    const lastIndex = index + this.length;

    return {
      next: () => ({
        value: this.#array[++index],
        done: index > lastIndex
      })
    };
  }
}
