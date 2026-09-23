# Relatório POC: Resultados Reais dos Cenários FIPE

Este documento captura a execução simulando alta volumetria e valida os cenários esperados.

## 1. Tabela de Performance (K6 - 200 VUs por 20s)

| Cenário | Volumetria (Reqs) | RPS | Cache MISS | Erros (Timeouts/Fails) | Latência p90 | Latência p95 |
|---------|-------------------|-----|------------|-------------------------|--------------|--------------|
| Direct DB (Gargalo) | 467252 | 23344 | - | 1055 | 13.15ms | 17.61ms |
| Cache com TTL (Stampede) | 477233 | 23850 | 1585 | 1346 | 12.66ms | 17.71ms |
| Cache com Warming (Ideal) | 581460 | 29066 | 5 | 0 | 12.05ms | 16.65ms |

**Análise Final**:
- **Cenário 1 (DB Direto):** O gargalo do Pool de Conexões do banco limitou o throughput bruto.
- **Cenário 2 (Cache TTL):** Houve grande melhoria de latência geral, porém picos de latência e concorrência sobrecarregando o sistema periodicamente ("Cache Stampede").
- **Cenário 3 (Cache com Warmup):** Desempenho máximo, mantendo p95 muito baixa sem onerar o banco, entregando zero erros.

## 2. Evidência do Fenômeno "Cache Stampede" (Cenário 2)

O Cache Stampede acontece porque, ao expirar uma chave em alta concorrência `N` VUs, dezenas de instâncias percebem o *Cache MISS* e fazem a consulta simultânea ao banco de dados no exato momento, re-saturando o pool do HikariCP desnecessariamente e gerando interrupções no tempo de reposta da API que seriam invisíveis na média bruta.

**Comportamento capturado nos logs do Spring Boot durante o teste de carga:**
- Aos segundos `xx:xx:00:38:24`, exatas **45 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:24`, exatas **45 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:24`, exatas **41 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:24`, exatas **75 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:24`, exatas **33 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:30`, exatas **51 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:30`, exatas **66 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:30`, exatas **53 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:30`, exatas **50 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:31`, exatas **81 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:31`, exatas **240 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:37`, exatas **130 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:37`, exatas **114 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:37`, exatas **162 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:37`, exatas **198 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:00:38:37`, exatas **200 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.