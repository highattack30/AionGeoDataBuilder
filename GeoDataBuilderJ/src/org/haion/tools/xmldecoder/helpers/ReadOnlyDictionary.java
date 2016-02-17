package org.haion.tools.xmldecoder.helpers;

public class ReadOnlyDictionary {
//    private IDictionary<TKey, TValue> Source;
//
//    public ICollection<TKey> Keys
//    {
//      get
//      {
//        return (ICollection<TKey>) new ReadOnlyCollection<TKey>((IList<TKey>) new List<TKey>((IEnumerable<TKey>) this.Source.Keys));
//      }
//    }
//
//    public ICollection<TValue> Values
//    {
//      get
//      {
//        return (ICollection<TValue>) new ReadOnlyCollection<TValue>((IList<TValue>) new List<TValue>((IEnumerable<TValue>) this.Source.Values));
//      }
//    }
//
//    public TValue this[TKey key]
//    {
//      get
//      {
//        return this.Source[key];
//      }
//    }
//
//    public int Count
//    {
//      get
//      {
//        return this.Source.Count;
//      }
//    }
//
//    public bool IsReadOnly
//    {
//      get
//      {
//        return true;
//      }
//    }
//
//    public ReadOnlyDictionary(IDictionary<TKey, TValue> source)
//    {
//      this.Source = source;
//    }
//
//    public void Add(TKey key, TValue value)
//    {
//      throw new NotSupportedException();
//    }
//
//    public bool ContainsKey(TKey key)
//    {
//      return this.Source.ContainsKey(key);
//    }
//
//    public bool Remove(TKey key)
//    {
//      throw new NotSupportedException();
//    }
//
//    public bool TryGetValue(TKey key, out TValue value)
//    {
//      return this.Source.TryGetValue(key, out value);
//    }
//
//    TValue IDictionary<TKey, TValue>.get_Item(TKey key)
//    {
//      return this[key];
//    }
//
//    void IDictionary<TKey, TValue>.set_Item(TKey key, TValue value)
//    {
//      throw new NotSupportedException();
//    }
//
//    public void Add(KeyValuePair<TKey, TValue> item)
//    {
//      throw new NotSupportedException();
//    }
//
//    public void Clear()
//    {
//      throw new NotSupportedException();
//    }
//
//    public bool Contains(KeyValuePair<TKey, TValue> item)
//    {
//      return this.Source.Contains(item);
//    }
//
//    public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
//    {
//      this.Source.CopyTo(array, arrayIndex);
//    }
//
//    public bool Remove(KeyValuePair<TKey, TValue> item)
//    {
//      throw new NotSupportedException();
//    }
//
//    public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator()
//    {
//      foreach (KeyValuePair<TKey, TValue> keyValuePair in (IEnumerable<KeyValuePair<TKey, TValue>>) this.Source)
//        yield return keyValuePair;
//    }
//
//    IEnumerator IEnumerable.GetEnumerator()
//    {
//      return (IEnumerator) this.GetEnumerator();
//    }
}
