import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select.jsx'
import { Textarea } from '@/components/ui/textarea.jsx'
import { Heart, Package, Users, ShoppingCart, UserPlus, Trash2, Edit } from 'lucide-react'
import './App.css'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userRole, setUserRole] = useState('')
  const [loginData, setLoginData] = useState({ username: '', password: '' })
  
  // Estados para cestas básicas
  const [cestas, setCestas] = useState([])
  const [novaCesta, setNovaCesta] = useState('')
  
  // Estados para beneficiados
  const [beneficiados, setBeneficiados] = useState([])
  const [novoBeneficiado, setNovoBeneficiado] = useState({
    nome: '',
    cpf: '',
    telefone: '',
    endereco: '',
    dataNascimento: '',
    rendaFamiliar: '',
    numeroDependentes: ''
  })
  
  // Estados para doações
  const [doacoes, setDoacoes] = useState([])
  const [novaDoacao, setNovaDoacao] = useState({
    tipoAlimento: '',
    quantidade: '',
    unidadeMedida: 'kg',
    dataRecebimento: '',
    dataValidade: '',
    origem: '',
    observacoes: ''
  })
  
  // Estados para usuários (admin)
  const [usuarios, setUsuarios] = useState([])
  const [novoUsuario, setNovoUsuario] = useState({
    username: '',
    password: '',
    email: '',
    role: 'USER'
  })

  const API_BASE = 'http://localhost:8080/api'

  const getAuthHeaders = () => {
    const token = localStorage.getItem('token')
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
      })
      
      if (response.ok) {
        const data = await response.json()
        localStorage.setItem('token', data.token)
        setUserRole(data.role)
        setIsLoggedIn(true)
        // Carregar dados iniciais
        carregarCestas()
        carregarBeneficiados()
        carregarDoacoes()
        if (data.role === 'ADMIN') {
          carregarUsuarios()
        }
      } else {
        alert('Erro no login. Verifique suas credenciais.')
      }
    } catch (error) {
      console.error('Erro no login:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  // Funções para cestas básicas
  const criarCesta = async () => {
    if (!novaCesta.trim()) return
    
    try {
      const response = await fetch(`${API_BASE}/cestas-basicas`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ nomeCesta: novaCesta }),
      })
      
      if (response.ok) {
        const cesta = await response.json()
        setCestas([...cestas, cesta])
        setNovaCesta('')
      } else {
        alert('Erro ao criar cesta básica.')
      }
    } catch (error) {
      console.error('Erro ao criar cesta:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const carregarCestas = async () => {
    try {
      const response = await fetch(`${API_BASE}/cestas-basicas`, {
        headers: getAuthHeaders(),
      })
      
      if (response.ok) {
        const data = await response.json()
        setCestas(data)
      }
    } catch (error) {
      console.error('Erro ao carregar cestas:', error)
    }
  }

  // Funções para beneficiados
  const criarBeneficiado = async () => {
    if (!novoBeneficiado.nome || !novoBeneficiado.cpf) {
      alert('Nome e CPF são obrigatórios')
      return
    }
    
    try {
      const response = await fetch(`${API_BASE}/beneficiados`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(novoBeneficiado),
      })
      
      if (response.ok) {
        const beneficiado = await response.json()
        setBeneficiados([...beneficiados, beneficiado])
        setNovoBeneficiado({
          nome: '',
          cpf: '',
          telefone: '',
          endereco: '',
          dataNascimento: '',
          rendaFamiliar: '',
          numeroDependentes: ''
        })
      } else {
        const error = await response.text()
        alert('Erro ao criar beneficiado: ' + error)
      }
    } catch (error) {
      console.error('Erro ao criar beneficiado:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const carregarBeneficiados = async () => {
    try {
      const response = await fetch(`${API_BASE}/beneficiados`, {
        headers: getAuthHeaders(),
      })
      
      if (response.ok) {
        const data = await response.json()
        setBeneficiados(data)
      }
    } catch (error) {
      console.error('Erro ao carregar beneficiados:', error)
    }
  }

  // Funções para doações
  const criarDoacao = async () => {
    if (!novaDoacao.tipoAlimento || !novaDoacao.quantidade) {
      alert('Tipo de alimento e quantidade são obrigatórios')
      return
    }
    
    try {
      const response = await fetch(`${API_BASE}/doacoes`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(novaDoacao),
      })
      
      if (response.ok) {
        const doacao = await response.json()
        setDoacoes([...doacoes, doacao])
        setNovaDoacao({
          tipoAlimento: '',
          quantidade: '',
          unidadeMedida: 'kg',
          dataRecebimento: '',
          dataValidade: '',
          origem: '',
          observacoes: ''
        })
      } else {
        const error = await response.text()
        alert('Erro ao criar doação: ' + error)
      }
    } catch (error) {
      console.error('Erro ao criar doação:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const carregarDoacoes = async () => {
    try {
      const response = await fetch(`${API_BASE}/doacoes`, {
        headers: getAuthHeaders(),
      })
      
      if (response.ok) {
        const data = await response.json()
        setDoacoes(data)
      }
    } catch (error) {
      console.error('Erro ao carregar doações:', error)
    }
  }

  // Funções para usuários (admin)
  const criarUsuario = async () => {
    if (!novoUsuario.username || !novoUsuario.password) {
      alert('Username e senha são obrigatórios')
      return
    }
    
    try {
      const response = await fetch(`${API_BASE}/users`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(novoUsuario),
      })
      
      if (response.ok) {
        const usuario = await response.json()
        setUsuarios([...usuarios, usuario])
        setNovoUsuario({
          username: '',
          password: '',
          email: '',
          role: 'USER'
        })
        alert('Usuário criado com sucesso!')
      } else {
        const error = await response.text()
        alert('Erro ao criar usuário: ' + error)
      }
    } catch (error) {
      console.error('Erro ao criar usuário:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const carregarUsuarios = async () => {
    try {
      const response = await fetch(`${API_BASE}/users`, {
        headers: getAuthHeaders(),
      })
      
      if (response.ok) {
        const data = await response.json()
        setUsuarios(data)
      }
    } catch (error) {
      console.error('Erro ao carregar usuários:', error)
    }
  }

  const deletarUsuario = async (userId) => {
    if (!confirm('Tem certeza que deseja deletar este usuário?')) return
    
    try {
      const response = await fetch(`${API_BASE}/users/${userId}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
      })
      
      if (response.ok) {
        setUsuarios(usuarios.filter(u => u.id !== userId))
        alert('Usuário deletado com sucesso!')
      } else {
        const error = await response.text()
        alert('Erro ao deletar usuário: ' + error)
      }
    } catch (error) {
      console.error('Erro ao deletar usuário:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  if (!isLoggedIn) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
              <Heart className="w-6 h-6 text-red-600" />
            </div>
            <CardTitle className="text-2xl">Sistema de Doações</CardTitle>
            <CardDescription>
              Faça login para gerenciar cestas básicas
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Usuário</Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="Digite seu usuário"
                  value={loginData.username}
                  onChange={(e) => setLoginData({...loginData, username: e.target.value})}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Senha</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Digite sua senha"
                  value={loginData.password}
                  onChange={(e) => setLoginData({...loginData, password: e.target.value})}
                  required
                />
              </div>
              <Button type="submit" className="w-full">
                Entrar
              </Button>
            </form>
            <div className="mt-4 p-3 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-700">
                <strong>Credenciais de teste:</strong><br />
                Usuário: admin<br />
                Senha: admin123
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Heart className="w-8 h-8 text-red-600 mr-3" />
              <h1 className="text-xl font-semibold text-gray-900">Sistema de Doações</h1>
              <Badge variant="outline" className="ml-3">{userRole}</Badge>
            </div>
            <Button 
              variant="outline" 
              onClick={() => {
                localStorage.removeItem('token')
                setIsLoggedIn(false)
                setUserRole('')
              }}
            >
              Sair
            </Button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Tabs defaultValue="cestas" className="space-y-6">
          <TabsList className={`grid w-full ${userRole === 'ADMIN' ? 'grid-cols-5' : 'grid-cols-4'}`}>
            <TabsTrigger value="cestas" className="flex items-center gap-2">
              <Package className="w-4 h-4" />
              Cestas Básicas
            </TabsTrigger>
            <TabsTrigger value="beneficiados" className="flex items-center gap-2">
              <Users className="w-4 h-4" />
              Beneficiados
            </TabsTrigger>
            <TabsTrigger value="doacoes" className="flex items-center gap-2">
              <ShoppingCart className="w-4 h-4" />
              Doações
            </TabsTrigger>
            <TabsTrigger value="relatorios" className="flex items-center gap-2">
              <Heart className="w-4 h-4" />
              Relatórios
            </TabsTrigger>
            {userRole === 'ADMIN' && (
              <TabsTrigger value="usuarios" className="flex items-center gap-2">
                <UserPlus className="w-4 h-4" />
                Usuários
              </TabsTrigger>
            )}
          </TabsList>

          {/* Tab Cestas Básicas */}
          <TabsContent value="cestas" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Cestas Básicas</CardTitle>
                <CardDescription>
                  Crie e gerencie cestas básicas para distribuição
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex gap-4 mb-6">
                  <Input
                    placeholder="Nome da nova cesta básica"
                    value={novaCesta}
                    onChange={(e) => setNovaCesta(e.target.value)}
                    className="flex-1"
                  />
                  <Button onClick={criarCesta}>
                    Criar Cesta
                  </Button>
                  <Button variant="outline" onClick={carregarCestas}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {cestas.map((cesta) => (
                    <Card key={cesta.id} className="border-l-4 border-l-blue-500">
                      <CardHeader className="pb-3">
                        <div className="flex justify-between items-start">
                          <CardTitle className="text-lg">{cesta.nomeCesta}</CardTitle>
                          <Badge variant={cesta.status === 'Montada' ? 'default' : 'secondary'}>
                            {cesta.status}
                          </Badge>
                        </div>
                        <CardDescription>
                          Data: {new Date(cesta.dataMontagem).toLocaleDateString('pt-BR')}
                        </CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="flex gap-2">
                          <Button size="sm" variant="outline">
                            Editar
                          </Button>
                          <Button size="sm" variant="outline">
                            Ver Itens
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>

                {cestas.length === 0 && (
                  <div className="text-center py-8 text-gray-500">
                    <Package className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhuma cesta básica encontrada.</p>
                    <p className="text-sm">Crie uma nova cesta para começar.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* Tab Beneficiados */}
          <TabsContent value="beneficiados" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Beneficiados</CardTitle>
                <CardDescription>
                  Cadastre e gerencie os beneficiados do programa
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                  <div className="space-y-2">
                    <Label htmlFor="nome">Nome *</Label>
                    <Input
                      id="nome"
                      placeholder="Nome completo"
                      value={novoBeneficiado.nome}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, nome: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="cpf">CPF *</Label>
                    <Input
                      id="cpf"
                      placeholder="000.000.000-00"
                      value={novoBeneficiado.cpf}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, cpf: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="telefone">Telefone</Label>
                    <Input
                      id="telefone"
                      placeholder="(00) 00000-0000"
                      value={novoBeneficiado.telefone}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, telefone: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataNascimento">Data de Nascimento</Label>
                    <Input
                      id="dataNascimento"
                      type="date"
                      value={novoBeneficiado.dataNascimento}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, dataNascimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="rendaFamiliar">Renda Familiar</Label>
                    <Input
                      id="rendaFamiliar"
                      type="number"
                      step="0.01"
                      placeholder="0.00"
                      value={novoBeneficiado.rendaFamiliar}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, rendaFamiliar: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="numeroDependentes">Número de Dependentes</Label>
                    <Input
                      id="numeroDependentes"
                      type="number"
                      placeholder="0"
                      value={novoBeneficiado.numeroDependentes}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, numeroDependentes: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2 md:col-span-2">
                    <Label htmlFor="endereco">Endereço</Label>
                    <Textarea
                      id="endereco"
                      placeholder="Endereço completo"
                      value={novoBeneficiado.endereco}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, endereco: e.target.value})}
                    />
                  </div>
                </div>
                
                <div className="flex gap-4 mb-6">
                  <Button onClick={criarBeneficiado}>
                    Cadastrar Beneficiado
                  </Button>
                  <Button variant="outline" onClick={carregarBeneficiados}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {beneficiados.map((beneficiado) => (
                    <Card key={beneficiado.id} className="border-l-4 border-l-green-500">
                      <CardHeader className="pb-3">
                        <CardTitle className="text-lg">{beneficiado.nome}</CardTitle>
                        <CardDescription>
                          CPF: {beneficiado.cpf}
                        </CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="space-y-1 text-sm">
                          {beneficiado.telefone && <p>Tel: {beneficiado.telefone}</p>}
                          {beneficiado.rendaFamiliar && <p>Renda: R$ {beneficiado.rendaFamiliar}</p>}
                          {beneficiado.numeroDependentes && <p>Dependentes: {beneficiado.numeroDependentes}</p>}
                        </div>
                        <div className="flex gap-2 mt-4">
                          <Button size="sm" variant="outline">
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button size="sm" variant="outline">
                            Ver Histórico
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>

                {beneficiados.length === 0 && (
                  <div className="text-center py-8 text-gray-500">
                    <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhum beneficiado cadastrado.</p>
                    <p className="text-sm">Cadastre um novo beneficiado para começar.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* Tab Doações */}
          <TabsContent value="doacoes" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Registrar Doações</CardTitle>
                <CardDescription>
                  Registre e gerencie doações recebidas
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                  <div className="space-y-2">
                    <Label htmlFor="tipoAlimento">Tipo de Alimento *</Label>
                    <Input
                      id="tipoAlimento"
                      placeholder="Ex: Arroz, Feijão, Óleo"
                      value={novaDoacao.tipoAlimento}
                      onChange={(e) => setNovaDoacao({...novaDoacao, tipoAlimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="quantidade">Quantidade *</Label>
                    <div className="flex gap-2">
                      <Input
                        id="quantidade"
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        value={novaDoacao.quantidade}
                        onChange={(e) => setNovaDoacao({...novaDoacao, quantidade: e.target.value})}
                        className="flex-1"
                      />
                      <Select value={novaDoacao.unidadeMedida} onValueChange={(value) => setNovaDoacao({...novaDoacao, unidadeMedida: value})}>
                        <SelectTrigger className="w-24">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="kg">kg</SelectItem>
                          <SelectItem value="g">g</SelectItem>
                          <SelectItem value="l">l</SelectItem>
                          <SelectItem value="ml">ml</SelectItem>
                          <SelectItem value="un">un</SelectItem>
                          <SelectItem value="cx">cx</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataRecebimento">Data de Recebimento</Label>
                    <Input
                      id="dataRecebimento"
                      type="date"
                      value={novaDoacao.dataRecebimento}
                      onChange={(e) => setNovaDoacao({...novaDoacao, dataRecebimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataValidade">Data de Validade</Label>
                    <Input
                      id="dataValidade"
                      type="date"
                      value={novaDoacao.dataValidade}
                      onChange={(e) => setNovaDoacao({...novaDoacao, dataValidade: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="origem">Origem da Doação</Label>
                    <Input
                      id="origem"
                      placeholder="Ex: Supermercado X, Pessoa Física"
                      value={novaDoacao.origem}
                      onChange={(e) => setNovaDoacao({...novaDoacao, origem: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="observacoes">Observações</Label>
                    <Textarea
                      id="observacoes"
                      placeholder="Observações adicionais"
                      value={novaDoacao.observacoes}
                      onChange={(e) => setNovaDoacao({...novaDoacao, observacoes: e.target.value})}
                    />
                  </div>
                </div>
                
                <div className="flex gap-4 mb-6">
                  <Button onClick={criarDoacao}>
                    Registrar Doação
                  </Button>
                  <Button variant="outline" onClick={carregarDoacoes}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {doacoes.map((doacao) => (
                    <Card key={doacao.id} className="border-l-4 border-l-orange-500">
                      <CardHeader className="pb-3">
                        <CardTitle className="text-lg">{doacao.tipoAlimento}</CardTitle>
                        <CardDescription>
                          {doacao.quantidade} {doacao.unidadeMedida}
                        </CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="space-y-1 text-sm">
                          {doacao.dataRecebimento && <p>Recebido: {new Date(doacao.dataRecebimento).toLocaleDateString('pt-BR')}</p>}
                          {doacao.dataValidade && <p>Validade: {new Date(doacao.dataValidade).toLocaleDateString('pt-BR')}</p>}
                          {doacao.origem && <p>Origem: {doacao.origem}</p>}
                        </div>
                        <div className="flex gap-2 mt-4">
                          <Button size="sm" variant="outline">
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button size="sm" variant="outline">
                            Usar em Cesta
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>

                {doacoes.length === 0 && (
                  <div className="text-center py-8 text-gray-500">
                    <ShoppingCart className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhuma doação registrada.</p>
                    <p className="text-sm">Registre uma nova doação para começar.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* Tab Relatórios */}
          <TabsContent value="relatorios" className="space-y-6">
            <div className="grid gap-6 md:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardTitle>Resumo Geral</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="flex justify-between">
                      <span>Total de Cestas:</span>
                      <Badge variant="outline">{cestas.length}</Badge>
                    </div>
                    <div className="flex justify-between">
                      <span>Total de Beneficiados:</span>
                      <Badge variant="outline">{beneficiados.length}</Badge>
                    </div>
                    <div className="flex justify-between">
                      <span>Total de Doações:</span>
                      <Badge variant="outline">{doacoes.length}</Badge>
                    </div>
                    {userRole === 'ADMIN' && (
                      <div className="flex justify-between">
                        <span>Total de Usuários:</span>
                        <Badge variant="outline">{usuarios.length}</Badge>
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Ações Rápidas</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <Button variant="outline" className="w-full justify-start">
                      Exportar Relatório de Beneficiados
                    </Button>
                    <Button variant="outline" className="w-full justify-start">
                      Exportar Relatório de Doações
                    </Button>
                    <Button variant="outline" className="w-full justify-start">
                      Relatório de Distribuições
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Tab Usuários (Admin) */}
          {userRole === 'ADMIN' && (
            <TabsContent value="usuarios" className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle>Gerenciar Usuários</CardTitle>
                  <CardDescription>
                    Crie e gerencie usuários do sistema (apenas administradores)
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                    <div className="space-y-2">
                      <Label htmlFor="username">Nome de Usuário *</Label>
                      <Input
                        id="username"
                        placeholder="nome_usuario"
                        value={novoUsuario.username}
                        onChange={(e) => setNovoUsuario({...novoUsuario, username: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="password">Senha *</Label>
                      <Input
                        id="password"
                        type="password"
                        placeholder="Senha segura"
                        value={novoUsuario.password}
                        onChange={(e) => setNovoUsuario({...novoUsuario, password: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input
                        id="email"
                        type="email"
                        placeholder="usuario@email.com"
                        value={novoUsuario.email}
                        onChange={(e) => setNovoUsuario({...novoUsuario, email: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="role">Função</Label>
                      <Select value={novoUsuario.role} onValueChange={(value) => setNovoUsuario({...novoUsuario, role: value})}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="USER">Usuário</SelectItem>
                          <SelectItem value="ADMIN">Administrador</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  
                  <div className="flex gap-4 mb-6">
                    <Button onClick={criarUsuario}>
                      Criar Usuário
                    </Button>
                    <Button variant="outline" onClick={carregarUsuarios}>
                      Atualizar Lista
                    </Button>
                  </div>

                  <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                    {usuarios.map((usuario) => (
                      <Card key={usuario.id} className="border-l-4 border-l-purple-500">
                        <CardHeader className="pb-3">
                          <div className="flex justify-between items-start">
                            <CardTitle className="text-lg">{usuario.username}</CardTitle>
                            <Badge variant={usuario.role === 'ADMIN' ? 'default' : 'secondary'}>
                              {usuario.role}
                            </Badge>
                          </div>
                          <CardDescription>
                            {usuario.email || 'Sem email'}
                          </CardDescription>
                        </CardHeader>
                        <CardContent>
                          <div className="flex gap-2">
                            <Button size="sm" variant="outline">
                              <Edit className="w-4 h-4" />
                            </Button>
                            <Button 
                              size="sm" 
                              variant="outline" 
                              onClick={() => deletarUsuario(usuario.id)}
                              className="text-red-600 hover:text-red-700"
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>

                  {usuarios.length === 0 && (
                    <div className="text-center py-8 text-gray-500">
                      <UserPlus className="w-12 h-12 mx-auto mb-4 opacity-50" />
                      <p>Nenhum usuário encontrado.</p>
                      <p className="text-sm">Crie um novo usuário para começar.</p>
                    </div>
                  )}
                </CardContent>
              </Card>
            </TabsContent>
          )}
        </Tabs>
      </main>
    </div>
  )
}

export default App

