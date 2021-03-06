package de.fxdiagram.xtext.xbase

import com.google.inject.Inject
import de.fxdiagram.core.anchors.LineArrowHead
import de.fxdiagram.core.anchors.TriangleArrowHead
import de.fxdiagram.eclipse.xtext.mapping.AbstractXtextDiagramConfig
import de.fxdiagram.mapping.ConnectionLabelMapping
import de.fxdiagram.mapping.ConnectionMapping
import de.fxdiagram.mapping.DiagramMapping
import de.fxdiagram.mapping.IMappedElementDescriptor
import de.fxdiagram.mapping.MappingAcceptor
import de.fxdiagram.mapping.NodeHeadingMapping
import de.fxdiagram.mapping.NodeLabelMapping
import de.fxdiagram.mapping.NodeMapping
import de.fxdiagram.mapping.shapes.BaseClassNode
import de.fxdiagram.mapping.shapes.BaseConnection
import de.fxdiagram.mapping.shapes.BaseDiagramNode
import javafx.scene.paint.Color
import org.eclipse.emf.ecore.EObject
import org.eclipse.jdt.core.IType
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.example.domainmodel.domainmodel.Entity
import org.eclipse.xtext.example.domainmodel.domainmodel.PackageDeclaration
import org.eclipse.xtext.resource.IResourceServiceProvider
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

import static de.fxdiagram.mapping.shapes.BaseClassNode.*

import static extension de.fxdiagram.core.extensions.ButtonExtensions.*
import org.eclipse.emf.common.util.URI

class JvmClassDiagramConfig extends AbstractXtextDiagramConfig {

	@Inject extension JvmDomainUtil
	
	@Inject extension IResourceServiceProvider.Registry

	val typeNode = new NodeMapping<JvmDeclaredType>(this, 'typeNode', 'Type') {
		override createNode(IMappedElementDescriptor<JvmDeclaredType> descriptor) {
			new BaseClassNode(descriptor)
		}

		override calls() {
			typeName.labelFor[it]
			packageName.labelFor[it]
			fileName.labelFor[it]
			attribute.labelForEach[attributes]
			operation.labelForEach[methods]
			referenceConnection.outConnectionForEach [
				references
			].asButton[getArrowButton("Add reference")]
			superTypeConnection.outConnectionForEach [ JvmDeclaredType it | 
				superTypes.filter[type instanceof JvmDeclaredType]
			].asButton[getTriangleButton("Add supertype")]
		}
	}
	
	val typeName = new NodeHeadingMapping<JvmDeclaredType>(this, CLASS_NAME) {
		override getText(JvmDeclaredType it) {
			simpleName
		}
	}

	val packageName = new NodeLabelMapping<JvmDeclaredType>(this, PACKAGE) {
		override getText(JvmDeclaredType it) {
			it.packageName
		}
	}

	val fileName = new NodeLabelMapping<JvmDeclaredType>(this, FILE_NAME) {
		override getText(JvmDeclaredType it) {
			eResource.URI.lastSegment
		}
	}

	val attribute = new NodeLabelMapping<JvmField>(this, ATTRIBUTE) {
		override getText(JvmField it) {
			simpleName + ': ' + type.simpleName
		}
	}

	val operation = new NodeLabelMapping<JvmOperation>(this, OPERATION) {
		override getText(JvmOperation it) {
			simpleName + '(): ' + returnType.simpleName
		}
	}

	val referenceConnection = new ConnectionMapping<JvmField>(this, 'referenceConnection', 'Reference') {
		override createConnection(IMappedElementDescriptor<JvmField> descriptor) {
			new BaseConnection(descriptor) => [
				targetArrowHead = new LineArrowHead(it, false)
			]
		}

		override calls() {
			referenceName.labelFor[it]
			typeNode.target[(type.componentType.type as JvmDeclaredType).originalJvmType]
		}
	}

	val referenceName = new ConnectionLabelMapping<JvmField>(this, 'referenceName') {
		override getText(JvmField it) {
			simpleName
		}
	}
	
	val superTypeConnection = new ConnectionMapping<JvmTypeReference>(this, 'superTypeConnection', 'Supertype') {
		override createConnection(IMappedElementDescriptor<JvmTypeReference> descriptor) {
			new BaseConnection(descriptor) => [
				targetArrowHead = new TriangleArrowHead(it, 10, 15, null, Color.WHITE, false)
				// TODO set strokeDashOffset for interfaces			
			]
		}

		override calls() {
			typeNode.target[(type as JvmDeclaredType).originalJvmType]
		}
	}
	
	val packageDiagram = new DiagramMapping<PackageDeclaration>(this, 'packageDiagram', 'Package diagram') {
		override calls() {
			typeNode.nodeForEach[elements.filter(Entity).map[primaryJvmElement].filter(JvmDeclaredType)]
			packageNode.nodeForEach[elements.filter(PackageDeclaration)]
			eagerly(superTypeConnection, referenceConnection)
		}
	}
	
	val packageNode = new NodeMapping<PackageDeclaration>(this, 'packageNode', 'Package node') {
		override createNode(IMappedElementDescriptor<PackageDeclaration> descriptor) {
			new BaseDiagramNode(descriptor)
		}

		override calls() {
			packageNodeName.labelFor[it]
			packageDiagram.nestedDiagramFor[it].onOpen
		}
	}

	val packageNodeName = new NodeHeadingMapping<PackageDeclaration>(this, BaseDiagramNode.NODE_HEADING) {
		override getText(PackageDeclaration element) {
			element.name.split('\\.').last
		}
	}

	protected def getPrimaryJvmElement(EObject element) {
		getResourceServiceProvider(element.eResource.URI)
			?.get(IJvmModelAssociations)
			?.getPrimaryJvmElement(element)
	}
	
	override protected <ARG> entryCalls(ARG domainArgument, extension MappingAcceptor<ARG> acceptor) {
		switch domainArgument {
			JvmDeclaredType:
				acceptor.add(typeNode)
			IType:
				acceptor.add(typeNode, [domainArgument.jvmType])
		 	PackageDeclaration:
		 		acceptor.add(packageNode)
		}
	}
	
	def getJvmType(IType type) {
		val resourceServiceProvider = IResourceServiceProvider.Registry.INSTANCE
			.getResourceServiceProvider(URI.createURI('dummy.___xbase'))
		resourceServiceProvider.get(JvmDomainUtil).getJvmElement(type) as JvmDeclaredType
	}
	
	override protected createDomainObjectProvider() {
		new JvmDomainObjectProvider
	}
	
}
