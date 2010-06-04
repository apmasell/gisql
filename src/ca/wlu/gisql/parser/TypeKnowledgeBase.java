package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.parser.descriptors.BracketedExpressionDescriptor;
import ca.wlu.gisql.parser.descriptors.LiteralTokenDescriptor;
import ca.wlu.gisql.parser.descriptors.type.ArrowDescriptor;
import ca.wlu.gisql.parser.descriptors.type.ListDescriptor;
import ca.wlu.gisql.parser.descriptors.type.MaybeDescriptor;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;

public class TypeKnowledgeBase extends
		ParserKnowledgebase<ca.wlu.gisql.ast.type.Type, TypeNesting> {

	private final List<TypeVariable> variables = new ArrayList<TypeVariable>();

	public TypeKnowledgeBase() {
		super(TypeNesting.values(), "");
		installOperator(ArrowDescriptor.self);
		installOperator(BracketedExpressionDescriptor.typedescriptor);
		installOperator(ListDescriptor.self);
		installOperator(MaybeDescriptor.self);
		installOperator(new LiteralTokenDescriptor<Type, TypeNesting>(TokenName
				.<Type, TypeNesting> get(), TypeNesting.Type));
	}

	@SuppressWarnings("deprecation")
	@Override
	Type makeApplication(Type left, Type right) {
		if (left instanceof TypeVariable && right instanceof TypeVariable) {
			TypeVariable destination = (TypeVariable) right;
			for (TypeClass<?> typeclass : (TypeVariable) left) {
				destination.addTypeClass(typeclass);
			}
			return destination;
		} else {
			return null;
		}
	}

	@Override
	Type makeBoolean(boolean b) {
		return null;
	}

	@Override
	Type makeName(String name) {
		if (name.length() == 1 && name.charAt(0) >= 'α'
				&& name.charAt(0) <= 'ω') {
			int index = name.charAt(0) - 'α';
			while (variables.size() < index + 1) {
				variables.add(new TypeVariable());
			}
			return variables.get(index);
		} else {
			Type type = Type.getTypeForName(name);
			if (type == null) {
				TypeClass<?> typeclass = TypeClass.getTypeClassForName(name);
				type = new TypeVariable(typeclass);
			}
			return type;
		}
	}

}
