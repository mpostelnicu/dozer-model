package nl.dries.wicket.hibernate.dozer.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedSet;
import org.hibernate.engine.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate collection types
 * 
 * @author dries
 */
public enum HibernateCollectionType
{
	/** */
	LIST(PersistentBag.class, ArrayList.class),
	/** */
	SET(PersistentSet.class, HashSet.class),
	/** */
	SORTED_SET(PersistentSortedSet.class, TreeSet.class);

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(HibernateCollectionType.class);

	/** Specific Hibernate collection class */
	private Class<? extends PersistentCollection> hibernateCollectionClass;

	/** Plain 'Java collections' type */
	@SuppressWarnings("rawtypes")
	private Class<? extends Collection> plainTypeClass;

	/**
	 * Construct
	 * 
	 * @param hibernateCollectionClass
	 * @param plainTypeClass
	 */
	private HibernateCollectionType(Class<? extends PersistentCollection> hibernateCollectionClass,
		@SuppressWarnings("rawtypes") Class<? extends Collection> plainTypeClass)
	{
		this.hibernateCollectionClass = hibernateCollectionClass;
		this.plainTypeClass = plainTypeClass;
	}

	/**
	 * Creates a instance of the {@link PersistentCollection} defined by this type
	 * 
	 * @param sessionImpl
	 *            {@link SessionImplementor}
	 * @return {@link PersistentCollection} instance
	 */
	public PersistentCollection createCollection(SessionImplementor sessionImpl)
	{
		PersistentCollection collection = null;

		try
		{
			Constructor<? extends PersistentCollection> constructor = hibernateCollectionClass
				.getConstructor(SessionImplementor.class);
			collection = constructor.newInstance(sessionImpl);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			LOG.error("Persistent collection type {} has no SessionImplementor constructor", hibernateCollectionClass);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e)
		{
			LOG.error("Cannot create collection instance of type " + hibernateCollectionClass, e);
		}

		return collection;
	}

	/**
	 * Creates a plain Java collection instance based on a {@link PersistentCollection} one
	 * 
	 * @param persistentCollection
	 *            the {@link PersistentCollection}
	 * @return plain collection
	 */
	public Collection<?> createPlainCollection(PersistentCollection persistentCollection)
	{
		Collection<?> collection = null;
		try
		{
			@SuppressWarnings("rawtypes")
			Constructor<? extends Collection> constructor = plainTypeClass.getConstructor(Collection.class);
			collection = constructor.newInstance(persistentCollection);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			LOG.error("Colection type {} has no Collection constructor", plainTypeClass);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e)
		{
			LOG.error("Cannot create collection instance of type " + plainTypeClass, e);
		}

		return collection;
	}

	/**
	 * Determine collection type based on input value
	 * 
	 * @param sourceFieldValue
	 *            the input {@link PersistentCollection}
	 * @return {@link HibernateCollectionType}
	 */
	public static HibernateCollectionType determineType(PersistentCollection sourceFieldValue)
	{
		final HibernateCollectionType type;

		if (sourceFieldValue instanceof PersistentSortedSet)
		{
			type = HibernateCollectionType.SORTED_SET;
		}
		else if (sourceFieldValue instanceof PersistentSet)
		{
			type = HibernateCollectionType.SET;
		}
		else
		{
			type = HibernateCollectionType.LIST;
		}

		return type;
	}
}
