<#list entityColumns as entityColumn>
	<#if sqlQuery?? && sqlQuery && (entityColumn.name != entityColumn.DBName)>
		<#assign finderFieldSuffix = finderFieldSQLSuffix />
	<#else>
		<#assign finderFieldSuffix = "" />
	</#if>

	<#if entityColumn.hasArrayableOperator()>
		if (${entityColumn.names}.length > 0) {
			query.append("(");

			<#if stringUtil.equals(entityColumn.type, "String")>
				for (int i = 0; i < ${entityColumn.names}.length; i++) {
					${entityColumn.type} ${entityColumn.name} = ${entityColumn.names}[i];

					<#include "persistence_impl_finder_arrayable_col.ftl">

					if ((i + 1) < ${entityColumn.names}.length) {
						query.append(<#if entityColumn.isArrayableAndOperator()>WHERE_AND<#else>WHERE_OR</#if>);
					}
				}
			<#else>
				query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_7${finderFieldSuffix});

				query.append(StringUtil.merge(${entityColumn.names}));

				query.append(")");
			</#if>

			query.append(")");

			<#if entityColumn_has_next>
				query.append(WHERE_AND);
			</#if>
		}
	<#else>
		<#include "persistence_impl_finder_col.ftl">
	</#if>
</#list>

<#if entityFinder.where?? && validator.isNotNull(entityFinder.getWhere())>
	query.append("${entityFinder.where}");
<#else>
	query.setStringAt(removeConjunction(query.stringAt(query.index() - 1)), query.index() - 1);
</#if>