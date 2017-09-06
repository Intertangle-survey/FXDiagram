package de.fxdiagram.eclipse.xtext.hyperlink

import com.google.inject.Inject
import com.google.inject.name.Named
import de.fxdiagram.eclipse.FXDiagramView
import de.fxdiagram.eclipse.xtext.XtextDomainObjectProvider
import de.fxdiagram.mapping.IMappedElementDescriptor
import de.fxdiagram.mapping.NodeMapping
import de.fxdiagram.mapping.XDiagramConfig
import de.fxdiagram.mapping.execution.EntryCall
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.text.Region
import org.eclipse.jface.text.hyperlink.IHyperlink
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.IWorkbench
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.resource.ILocationInFileProvider
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper
import org.eclipse.xtext.util.ITextRegion

class FXDiagramHyperlinkHelper extends HyperlinkHelper {
	
	public static val DELEGATE = 'de.fxdiagram.eclipse.xtext.FXDiagramHyperlinkHelper.Delegate'

	@Inject@Named(DELEGATE) IHyperlinkHelper delegate

	@Inject IWorkbench workbench

	@Inject ILocationInFileProvider locationInFileProvider

	override createHyperlinksByOffset(XtextResource resource, int offset, boolean createMultipleHyperlinks) {
		val hyperlinks = delegate.createHyperlinksByOffset(resource, offset, createMultipleHyperlinks).emptyListIfNull
			+ super.createHyperlinksByOffset(resource, offset, createMultipleHyperlinks).emptyListIfNull
		if(hyperlinks.empty)
			null
		else 
			hyperlinks
	}
	
	protected def <T> List<T> emptyListIfNull(T[] array) {
		array as List<T> ?: emptyList
	}
	
	override createHyperlinksByOffset(XtextResource resource, int offset, IHyperlinkAcceptor acceptor) {
		val selectedElement = EObjectAtOffsetHelper.resolveElementAt(resource, offset)
		val editor = workbench?.activeWorkbenchWindow?.activePage?.activeEditor
		if (selectedElement !== null) {
			val entryCalls = XDiagramConfig.Registry.instance.configurations
				.map[getEntryCalls(selectedElement)]
				.flatten
			if (!entryCalls.empty) {
				val region = locationInFileProvider.getSignificantTextRegion(selectedElement)
				for (entryCall : entryCalls) {
					val domainObjectProvider = entryCall.config.domainObjectProvider
					if(domainObjectProvider instanceof XtextDomainObjectProvider) {
						val descriptor = entryCall.config.domainObjectProvider.createMappedElementDescriptor(selectedElement, new NodeMapping(entryCall.config, 'dummy', 'dummy'))
						acceptor.accept(new FXDiagramHyperlink(descriptor, entryCall, region, editor))
					}
				}
			}
		}
	}

	@Data
	static class FXDiagramHyperlink implements IHyperlink {

		val IMappedElementDescriptor<EObject> descriptor 
		val EntryCall<EObject> entryCall
		val ITextRegion region
		val IEditorPart editor

		override getHyperlinkRegion() {
			new Region(region.offset, region.length)
		}

		override getHyperlinkText() {
			'Show in FXDiagram as ' + entryCall.text
		}

		override getTypeLabel() {
			'FXDiagram'
		}

		override open() {
			val view = editor?.site?.workbenchWindow?.workbench?.activeWorkbenchWindow?.activePage
				?.showView("de.fxdiagram.eclipse.FXDiagramView")
			if (view instanceof FXDiagramView) {
				descriptor.withDomainObject [
					view.revealElement(it, entryCall, editor)
					null
				]
			}
		}
	}
}