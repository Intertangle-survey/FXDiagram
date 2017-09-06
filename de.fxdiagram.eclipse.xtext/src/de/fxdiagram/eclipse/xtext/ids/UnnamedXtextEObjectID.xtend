package de.fxdiagram.eclipse.xtext.ids

import de.fxdiagram.annotations.properties.ModelNode
import java.util.NoSuchElementException
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.resource.IResourceDescriptions

@ModelNode
class UnnamedXtextEObjectID extends AbstractXtextEObjectID {
	new(EClass eClass, URI elementURI) {
		super(eClass, elementURI)
	}
	
	override getQualifiedName() {
		null
	}
	
	override findInIndex(IResourceDescriptions index) {
		val resourceDescription = index.getResourceDescription(URI.trimFragment)
		resourceDescription.getExportedObjectsByType(EClass).findFirst[URI == EObjectURI]
	}
	
	override resolve(ResourceSet resourceSet) {
		val element = resourceSet.getEObject(URI, true)
		if(element === null || element.eIsProxy)
			throw new NoSuchElementException('Could not resolve ' + URI)
		if(!EClass.isInstance(element))
			throw new NoSuchElementException('Expected ' + EClass.name + ' but got ' + element.eClass.name)
		return element
	}
	
	override equals(Object obj) {
		if(obj instanceof UnnamedXtextEObjectID)
			return super.equals(obj) && this.URI == obj.URI
		else
			return false
	}
}