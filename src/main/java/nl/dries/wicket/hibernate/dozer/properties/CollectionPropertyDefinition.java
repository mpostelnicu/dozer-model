package nl.dries.wicket.hibernate.dozer.properties;

import java.io.Serializable;
import java.lang.reflect.Field;

import nl.dries.wicket.hibernate.dozer.helper.CollectionType;

/**
 * Collecition property definition
 * 
 * @author dries
 */
public class CollectionPropertyDefinition extends AbstractPropertyDefinition
{
	/** Default */
	private static final long serialVersionUID = 1L;

	/** Collection type */
	private CollectionType type;

	/**
	 * Construct
	 * 
	 * @param owner
	 *            the {@link Class} of the property owner
	 * @param ownerId
	 *            it's id
	 * @param property
	 *            the name of the field
	 * @param type
	 *            {@link CollectionType}
	 */
	public CollectionPropertyDefinition(Class<? extends Serializable> owner, Serializable ownerId, String property,
		CollectionType type)
	{
		super(owner, ownerId, property);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public CollectionType getCollectionType()
	{
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CollectionType type)
	{
		this.type = type;
	}

	/**
	 * @return role property (for a collection)
	 */
	public String getRole()
	{
		return getPropertyOwnerClass(getOwner()) + "." + getProperty();
	}

	/**
	 * Determines the owner of the current property (could be a superclass of the current class)
	 * 
	 * @param clazz
	 *            {@link Class}
	 * @return found ownen (class name)
	 */
	private String getPropertyOwnerClass(Class<?> clazz)
	{
		for (Field field : clazz.getDeclaredFields())
		{
			if (getProperty().equals(field.getName()))
			{
				return clazz.getName();
			}
		}

		return getPropertyOwnerClass(clazz.getSuperclass());
	}
}
