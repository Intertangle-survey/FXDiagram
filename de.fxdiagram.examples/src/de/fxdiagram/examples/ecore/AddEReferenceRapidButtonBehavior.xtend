package de.fxdiagram.examples.ecore

import de.fxdiagram.core.XConnection
import de.fxdiagram.core.XConnectionLabel
import de.fxdiagram.core.XRapidButton
import de.fxdiagram.core.anchors.DiamondArrowHead
import de.fxdiagram.core.anchors.LineArrowHead
import de.fxdiagram.lib.tools.CarusselChooser
import java.util.List
import java.util.Set
import javafx.collections.ListChangeListener
import org.eclipse.emf.ecore.EReference

import static de.fxdiagram.core.extensions.ButtonExtensions.*
import static javafx.geometry.Side.*

import static extension de.fxdiagram.core.extensions.CoreExtensions.*
import de.fxdiagram.core.behavior.AbstractHostBehavior

class AddEReferenceRapidButtonBehavior extends AbstractHostBehavior<EClassNode> {
	
	List<XRapidButton> buttons
	
	Set<EReference> availableKeys = newLinkedHashSet
	Set<EReference> unavailableKeys = newHashSet
	
	new(EClassNode host) {
		super(host)
	}
	
	override getBehaviorKey() {
		AddEReferenceRapidButtonBehavior
	}
	
	override protected doActivate() {
		availableKeys += host.EClass.EReferences
		if(!availableKeys.empty) {
			val addConnectionAction = [
				XRapidButton button |
				createChooser(button)
			]
			buttons = createButtons(addConnectionAction)
			host.diagram.buttons += buttons
			host.diagram.connections.addListener [
				ListChangeListener.Change<? extends XConnection> change |
				while(change.next) {
					if(change.wasAdded) 
						change.addedSubList.forEach[ 
							if(availableKeys.remove(key))
								unavailableKeys.add(key as EReference)
						]
					if(change.wasRemoved) 
						change.removed.forEach[
							if(unavailableKeys.remove(key))
								availableKeys.add(key as EReference)
						]
				}
				if(availableKeys.empty)
					host.diagram.buttons -= buttons
			]  			
		}
	}
	
	protected def createButtons((XRapidButton)=>void addReferencesAction) {
		#[
			new XRapidButton(host, 0, 0.5, getArrowButton(LEFT, 'Discover properties'), addReferencesAction),
			new XRapidButton(host, 1, 0.5, getArrowButton(RIGHT, 'Discover properties'), addReferencesAction)
		]
	}
	
	protected def createChooser(XRapidButton button) {
		val chooser = new CarusselChooser(host, button.getChooserPosition)
		availableKeys.forEach [
			chooser.addChoice(new EClassNode(it.EReferenceType), it)
		]
		chooser.connectionProvider = [
			host, choice, choiceInfo |
			val reference = choiceInfo as EReference 
			new XConnection(host, choice, reference) => [
				targetArrowHead = if (reference.container)
						new DiamondArrowHead(it, false)
					else 
						new LineArrowHead(it, false)
				sourceArrowHead = if (reference.containment) 
						new DiamondArrowHead(it, true)
					else if(!reference.container && reference.EOpposite != null) 
						new LineArrowHead(it, true)

				new XConnectionLabel(it) => [
					text.text = reference.name
					position = 0.8
				]
				if(reference.EOpposite != null) {
					new XConnectionLabel(it) => [
						text.text = reference.EOpposite.name
						position = 0.2
					]
				}
			]
		]
		host.root.currentTool = chooser
	}	
}