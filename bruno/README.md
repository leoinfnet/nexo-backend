# Coleção Bruno do Nexo

1. Inicie o PostgreSQL e o backend conforme `docs/GUIA_OPERACIONAL.md`.
2. No Bruno, escolha **Open Collection** e selecione esta pasta `bruno`.
3. Selecione o ambiente **Local**.
4. Execute requests individualmente ou rode a coleção na ordem configurada.

Os requests de criação salvam automaticamente os IDs no ambiente. A coleção cria
recursos separados para liquidação, cancelamento e exclusão, evitando conflitos
entre as regras de estado das contas.

As datas dos relatórios ficam em `bruno/environments/Local.bru` e podem ser
ajustadas sem editar os requests.

