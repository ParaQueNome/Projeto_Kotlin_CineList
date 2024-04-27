package com.example.cinelist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import android.app.AlertDialog
import android.graphics.PorterDuff
import android.text.InputType
import android.widget.EditText

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class tela_lista : AppCompatActivity() {

    private lateinit var listaTarefas: ListView
    private lateinit var textVazio: TextView
    private lateinit var botaoTarefas: Button
    private lateinit var filmes: ArrayList<String>
    private lateinit var adaptador: ArrayAdapter<String>
    private lateinit var banco: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_lista)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageView = findViewById<ImageView>(R.id.imageIcone)
        imageView.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_IN)

        banco = getSharedPreferences("MinhaLista", Context.MODE_PRIVATE)
        filmes = carregarFilmes()
        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, filmes)


        listaTarefas = findViewById(R.id.listaTarefas)
        listaTarefas.adapter = adaptador

        textVazio = findViewById(R.id.textVazio)
        botaoTarefas = findViewById(R.id.botaoTarefas)

        botaoTarefas.setOnClickListener {
            adicionarFilmes()
        }
        atualizarFilme()

        listaTarefas.setOnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(this)
                .setMessage("Deseja excluir esse Filme?")
                .setPositiveButton("Sim") { dialog, which ->
                    filmes.removeAt(position)
                    salvarFilmes()
                    adaptador.notifyDataSetChanged()
                    atualizarFilme()
                }
                .setNegativeButton("Não") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }


    private fun carregarFilmes(): ArrayList<String> {
        val gson = Gson()
        val json = banco.getString("Filmes", null)
        return if (json != null) {
            gson.fromJson(json, Array<String>::class.java).toCollection(ArrayList())
        } else {
            ArrayList()
        }
    }

    private fun salvarFilmes() {
        val gson = Gson()
        val json = gson.toJson(filmes)
        banco.edit().putString("Filmes", json).apply()
    }

    private fun adicionarFilmes() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Novo Filme")

        val inputNome = EditText(this)
        inputNome.inputType = InputType.TYPE_CLASS_TEXT
        inputNome.hint = "Nome do Filme"

        val inputAno = EditText(this)
        inputAno.inputType = InputType.TYPE_CLASS_NUMBER
        inputAno.hint = "Ano de Lançamento"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(inputNome)
        layout.addView(inputAno)
        builder.setView(layout)

        builder.setPositiveButton("Adicionar") { _, _ ->
            val nomeFilme = inputNome.text.toString()
            val anoLancamento = inputAno.text.toString()
            if (nomeFilme.isNotEmpty() && anoLancamento.isNotEmpty()) {
                val novoFilme = "$nomeFilme - $anoLancamento"
                filmes.add(novoFilme)
                salvarFilmes()
                adaptador.notifyDataSetChanged()
                atualizarFilme()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }


    private fun atualizarFilme(){
        if (filmes.isEmpty()){
            listaTarefas.visibility = View.GONE
            textVazio.visibility = View.VISIBLE
        } else {
            listaTarefas.visibility = View.VISIBLE
            textVazio.visibility = View.GONE
        }
    }
}