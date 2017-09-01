# AliasGithub
Trabalho da disciplina de Manutenção e Evolução de Software. O objetivo é fornecer uma ferramenta que possibilite a identificação de alias de usuários no GitHub.

## Introdução 
Um situação em que podemos encontrar alias ocorre quando um mesmo desenvolvedor possui emails e/ou nomes diferentes (portanto IDs diferentes). A identificação desses aliases é de extrema importância, visto que muitas tarefas dependem da identificação de usuários no GitHub, o que inclui tarefas de manutenção ou evolução de software.

## Heurística Proposta
A fim de identificar alias em repositórios do GitHub, foi implementada uma heurística que utiliza os nomes e emails dos usuários que realizaram commits no repositório. Este algoritmo faz uso da biblioteca [JGit](https://github.com/eclipse/jgit), uma implementação do Git completamente em Java, e de uma [biblioteca externa](https://github.com/tdebatty/java-string-similarity) para computar a similaridade entre string (é utilizado o algoritmo de Levenshtein normalizado).

A cada commit processado, verifica-se se o usuário que realizou o commit é um alias de algum outro usuário já presente na lista de usuários que realizaram os commits. Caso seja um alias, insere-se este usuário (do commit que está sendo processado) na lista de alias do usuário que é semelhante. Caso o resultado seja negativo (não é alias), este usuário é apenas inserido na lista de usuários que realizaram commit. A heurística compara o nome e email do usuário que realizou o commit que está sendo processado com nomes e emails na lista de usuários que já commitaram. Na explicação abaixo, considere o prefixo do email como a string que vem antes do @. As seguintes situações possíveis são analisadas e tratadas pela heurística

1. Caso o email seja exatamente igual (match) e os nomes diferentes, a heurística retorna o usuário como um alias do usuário que encontra-se na lista de usuários que realizaram commit.

2. Caso o email seja diferente, tem-se duas situações possíveis.
  
  * Se a similaridade entre o nome do usuário do commit em questão e algum prefixo do email dos usuários que estão na lista for maior do que 0.93, a heurística retorna este usuário como um alias.
  
  * Se a similaridade entre o prefixo do email do usuário deste commit e algum nome dos usuários que se encontram na lista for maior do que 0.93, a heurística retorna este usuário como um alias.
  
O valor 0.93 foi utilizado como threshold pois ele é indicado pelos próprios autores do método de Levenshtein.

## Como Utilizar
A fim de utilizar a ferramenta, basta clonar o repositório, importar para o eclipse e executar a classe principal: **Alias.java**. É necessário observar a entrada e saída que a ferramenta precisa e fornece:

> Entrada: URL do repositório a ser analisado.

> Saída: lista de alias (nome e email).

## Exemplo de Uso
Como exemplo de uso da ferramenta, ela foi utilizada no repositório **BroadleafCommerce**. Este respositório possui um total de 14.835 commits e 55 contribuidores, e a ferramenta forneceu 13 alias. A seguir, pode-se ver a URL utilizada como entrada bem como a saída fornecida pela ferramenta.

URL de entrada: https://github.com/BroadleafCommerce/BroadleafCommerce

Saída fornecida pela ferramenta:

---------------------- ALIAS ----------------------

_Alias 1_

Denis Cucchietti - denis.cucchietti@atos.net

Cucchietti - denis.cucchietti@atos.net

_Alias 2_

Chris Kittrell - ckittrell@broadleafcommerce.com

ckittrell - ckittrell@broadleafcommerce.com

_Alias 3_

samador88 - samador@broadleafcommerce.com

Sam Amador - samador@broadleafcommerce.com

_Alias 4_

Nicholas Crum - ncrum@broadleafcommerce.com

ncrum - ncrum@broadleafcommerce.com

Nick Crum - ncrum@broadleafcommerce.com

_Alias 5_

nathandmoore - moorednathan@gmail.com

Nathan Moore - moorednathan@gmail.com

_Alias 6_

danielcolgrove - dcolgrove@broadleafcommerce.com

Daniel Colgrove - dcolgrove@broadleafcommerce.com

_Alias 7_

charchar - charchar@broadleafcommerce.org

Chad Harchar - charchar@broadleafcommerce.org

_Alias 8_

ktisdell - ktisdell@broadleafcommerce.com

Kelly Tisdell - ktisdell@broadleafcommerce.com

Kelly Tisdell - ktisdell@credera.com

_Alias 9_

oleksiimiroshnyk - omiroshnyk@broadleafcommerce.com

Oleksii - omiroshnyk@broadleafcommerce.com

Oleksii Miroshnyk - omiroshnyk@broadleafcommerce.com

_Alias 10_

Reginald C Cole - rcole@broadleafcommerce.com

Reggie - rcole@broadleafcommerce.com

_Alias 11_

JaciEck - jeckert@utexas.edu

Jaci Eckert - jeckert@utexas.edu

_Alias 12_

jfleschler - jon.fleschler@gmail.com

Jonathan Fleschler - jon.fleschler@gmail.com

Jon Fleschler - jon.fleschler@gmail.com

_Alias 13_

jefffischer - jfischer@broadleafcommerce.org

Jeff Fischer - jfischer@broadleafcommerce.org



